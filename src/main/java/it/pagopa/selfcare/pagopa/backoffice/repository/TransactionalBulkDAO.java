package it.pagopa.selfcare.pagopa.backoffice.repository;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.fields;

@Slf4j
@Component
public class TransactionalBulkDAO implements Closeable {

    @Autowired
    private MongoClientSettings mongoClientSettings;

    @Value("${spring.data.mongodb.database}")
    private String dbName;

    @Value("${extraction.ibans.persistIbanBatchSize}")
    private Integer ibansBatchSize;

    private MongoClient client;

    private MongoCollection<BrokerIbansEntity> collection;

    public void init() {
        try {
            client = MongoClients.create(mongoClientSettings);
            collection = client.getDatabase(dbName).getCollection("brokerIbans", BrokerIbansEntity.class);
        } catch (Exception e) {
            log.error("[Export IBANs] - An error occurred while persisting new extraction!", e);
        }
    }

    public Set<String> getAllBrokerCodeGreaterThan(Date fromDate) {
        Set<String> results = new HashSet<>();
        BasicDBObject query = new BasicDBObject();
        query.put("createdAt", new BasicDBObject("$gte", fromDate));

        BasicDBObject fields = new BasicDBObject();
        fields.put(Constants.BROKER_CODE_DB_FIELD, 1);
        fields.put("_id", 0);

        FindIterable<BrokerIbansEntity> entities = collection.find(query).projection(fields);
        for (BrokerIbansEntity entity : entities) {
            results.add(entity.getBrokerCode());
        }
        return results;
    }

    public void save(BrokerIbansEntity entity) {
        String brokerCode = entity.getBrokerCode();
        log.debug(String.format("[Export IBANs] - Persisting the IBANs extraction for broker [%s]. A delete and re-persist will be executed...", brokerCode));
        long startTime = Calendar.getInstance().getTimeInMillis();
        ClientSession session = client.startSession();
        try {
            // starting transaction
            session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
            // deleting old document
            collection.deleteOne(Filters.eq(Constants.BROKER_CODE_DB_FIELD, brokerCode).toBsonDocument());
            // persisting new entity, not including the iban list
            int totalSize = entity.getIbans().size();
            if (totalSize <= ibansBatchSize) {
                collection.insertOne(entity);                
            } else {
                log.debug("[Export IBANs] - The number of IBANs is greater than [%d] elements. Persisting it in partition mode.");
                bulkInsert(entity);
            }
            // closing and committing transaction
            session.commitTransaction();
        } catch (MongoException e) {
            log.error("[Export IBANs] - An error occurred while persisting new extractions!", e);
            session.abortTransaction();
        } finally {
            log.info(String.format("[Export IBANs] - Persistence of IBANs extraction for broker [%s] ended in [%d] ms!", brokerCode, Utility.getTimelapse(startTime)));
            session.close();
        }
    }
    
    private void bulkInsert(BrokerIbansEntity entity) {
        BrokerIbansEntity partialEntity = BrokerIbansEntity.builder()
                .id(entity.getId())
                .brokerCode(entity.getBrokerCode())
                .createdAt(entity.getCreatedAt())
                .ibans(new ArrayList<>())
                .build();
        collection.insertOne(partialEntity);
        // updating new entity, partitioning the persistence of the list of ibans in fixed block size (avoiding error 413 RequestEntityTooLarge)
        int totalSize = entity.getIbans().size();
        for (int i = 0; i < totalSize; i += ibansBatchSize) {
            List<BrokerIbanEntity> partition = entity.getIbans().subList(i, Math.min(i + ibansBatchSize, totalSize));
            collection.updateOne(new Document(Constants.BROKER_CODE_DB_FIELD, entity.getBrokerCode()), Updates.addEachToSet("ibans", partition));
        }
    }

    public void clean(Date olderThan) {
        log.debug(String.format("[Export IBANs] - Cleaning all extractions older than [%s] date...", new SimpleDateFormat(Constants.DATE_FORMAT).format(olderThan)));
        long startTime = Calendar.getInstance().getTimeInMillis();
        ClientSession session = client.startSession();
        try {
            // starting transaction
            session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
            // deleting old document and save new one
            collection.deleteMany(Filters.lte("createdAt", olderThan).toBsonDocument());
            // closing and committing transaction
            session.commitTransaction();
        } catch (MongoException e) {
            log.error(String.format("[Export IBANs] - An error occurred while cleaning all extractions older than [%s] date!", new SimpleDateFormat(Constants.DATE_FORMAT).format(olderThan)), e);
            session.abortTransaction();
        } finally {
            log.debug(String.format("[Export IBANs] - Cleaning of all extractions older than [%s] ended in [%d] ms!", new SimpleDateFormat(Constants.DATE_FORMAT).format(olderThan), Utility.getTimelapse(startTime)));
            session.close();
        }
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
