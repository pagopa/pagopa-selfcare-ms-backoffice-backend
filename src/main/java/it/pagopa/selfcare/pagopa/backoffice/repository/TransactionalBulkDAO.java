package it.pagopa.selfcare.pagopa.backoffice.repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.InsertManyOptions;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Slf4j
@Component
public class TransactionalBulkDAO {

    @Autowired
    private MongoClientSettings mongoClientSettings;

    @Value("${spring.data.mongodb.database}")
    private String dbName;

    public void saveAll(List<BrokerIbansEntity> entities) {
        log.debug(String.format("[Export IBANs] - Persisting a set of [%d] extractions. A delete and re-persist will be executed...", entities.size()));
        long startTime = Calendar.getInstance().getTimeInMillis();
        try (MongoClient client = MongoClients.create(mongoClientSettings)) {
            MongoDatabase db = client.getDatabase(dbName);
            MongoCollection<BrokerIbansEntity> collection = db.getCollection("brokerIbans", BrokerIbansEntity.class);
            ClientSession session = client.startSession();
            try {
                // starting transaction
                session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
                // deleting all old documents
                long deletionStartTime = Calendar.getInstance().getTimeInMillis();
                log.debug("[Export IBANs] - Deleting all previous extractions...");
                collection.deleteMany(new BsonDocument());
                log.info(String.format("[Export IBANs] - Deletion of all previous extractions completed successfully in [%d] ms!", Utility.getTimelapse(deletionStartTime)));
                // persisting all new documents
                InsertManyOptions insertOptions = new InsertManyOptions();
                insertOptions.ordered(true);
                long persistStartTime = Calendar.getInstance().getTimeInMillis();
                log.debug(String.format("[Export IBANs] - Persisting a set of [%d] extractions...", entities.size()));
                collection.insertMany(entities, insertOptions);
                log.info(String.format("[Export IBANs] - Persistence of extractions completed successfully in [%d] ms!", Utility.getTimelapse(persistStartTime)));
                // closing and committing transaction
                session.commitTransaction();
            } catch (MongoException e) {
                log.error("[Export IBANs] - An error occurred while persisting new extractions!", e);
                session.abortTransaction();
            } finally {
                log.info(String.format("[Export IBANs] - Complete persistence of broker's IBANs extractions (delete and persist) ended in [%d] ms!", Utility.getTimelapse(startTime)));
                session.close();
            }
        } catch (Exception e) {
            log.error("[Export IBANs] - An error occurred while persisting new extractions!", e);
        }
    }

}
