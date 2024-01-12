package it.pagopa.selfcare.pagopa.backoffice.repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TransactionalBulkDAO implements Closeable {

    @Autowired
    private MongoClientSettings mongoClientSettings;

    @Value("${spring.data.mongodb.database}")
    private String dbName;

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

    public void save(BrokerIbansEntity entity) {
        String brokerCode = entity.getBrokerCode();
        log.debug(String.format("[Export IBANs] - Persisting the IBANs extraction for broker [%s]. A delete and re-persist will be executed...", brokerCode));
        long startTime = Calendar.getInstance().getTimeInMillis();
        ClientSession session = client.startSession();
        try {
            // starting transaction
            session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
            // deleting old document and save new one
            collection.deleteOne(Filters.eq("brokerCode", brokerCode).toBsonDocument());
            collection.insertOne(entity);
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

    public void clean(Date olderThan) {
        log.debug(String.format("[Export IBANs] - Cleaning all extractions older than [%s] date...", new SimpleDateFormat("yyyy-MM-dd").format(olderThan)));
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
            log.error(String.format("[Export IBANs] - An error occurred while cleaning all extractions older than [%s] date!", new SimpleDateFormat("yyyy-MM-dd").format(olderThan)), e);
            session.abortTransaction();
        } finally {
            log.debug(String.format("[Export IBANs] - Cleaning of all extractions older than [%s] ended in [%d] ms!", new SimpleDateFormat("yyyy-MM-dd").format(olderThan), Utility.getTimelapse(startTime)));
            session.close();
        }
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
