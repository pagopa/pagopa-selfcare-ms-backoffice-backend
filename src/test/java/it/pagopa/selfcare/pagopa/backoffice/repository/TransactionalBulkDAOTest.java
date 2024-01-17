package it.pagopa.selfcare.pagopa.backoffice.repository;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.mongodb.*;
import com.mongodb.client.*;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import lombok.SneakyThrows;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
class TransactionalBulkDAOTest {


    @MockBean
    private MongoDatabase db;

    @MockBean
    private MongoCollection<BrokerIbansEntity> collection;

    @MockBean
    private ClientSession session;

    @Autowired
    @InjectMocks
    private TransactionalBulkDAO dao;

    private final ListAppender<ILoggingEvent> listAppender = getMockLoggerListAppender();

    @Test
    void save_errorInInit() {

        ReflectionTestUtils.setField(dao, "dbName", "fake");

        try (MockedStatic<MongoClients> mockStatic = Mockito.mockStatic(MongoClients.class)) {

            // mocking components
            MongoClient client = spy(MongoClient.class);
            mockStatic.when(() -> MongoClients.create(any(MongoClientSettings.class))).thenReturn(client);

            // executing logic to be checked
            dao.init();

            // check assertions
            List<ILoggingEvent> logsList = listAppender.list;
            ILoggingEvent log = logsList.get(0);
            assertTrue("The log message does not contains the required string", log.getMessage().contains("[Export IBANs] - An error occurred"));
            assertEquals("The log message does not contains the required error level string", Level.ERROR, log.getLevel());
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "0,10",
            "5,10",
            "10,10",
            "20,10"
    })
    void save_ok(int numberOfIbans, int ibansBatchSize) {

        ReflectionTestUtils.setField(dao, "ibansBatchSize", ibansBatchSize);

        try (MockedStatic<MongoClients> mockStatic = Mockito.mockStatic(MongoClients.class)) {

            // mocking components
            MongoClient client = mockMongoClientInit(mockStatic);

            // executing logic to be checked
            dao.init();
            dao.save(getBrokerIbansEntity(numberOfIbans));
            dao.close();

            // check assertions
            verify(session, times(1)).startTransaction(any(TransactionOptions.class));
            verify(session, times(1)).commitTransaction();
            verify(session, times(0)).abortTransaction();
            verify(session, times(1)).close();
            verify(client, times(1)).close();
            verify(collection, times(1)).deleteOne(any(Bson.class));
            verify(collection, times(1)).insertOne(any(BrokerIbansEntity.class));
            if (numberOfIbans > ibansBatchSize) {
                verify(collection, times(numberOfIbans / ibansBatchSize)).updateOne(any(Bson.class), any(Bson.class));
            }
            ILoggingEvent logLine1 = getLog();
            assertTrue("The log message does not contains the required string", logLine1.getMessage().contains("[Export IBANs] - Persisting"));
            assertEquals("The log message does not contains the required error level string", Level.DEBUG, logLine1.getLevel());
            if (numberOfIbans > ibansBatchSize) {
                ILoggingEvent logLine2 = getLog();
                assertTrue("The log message does not contains the required string", logLine2.getMessage().contains("[Export IBANs] - The number of IBANs is greater than"));
                assertEquals("The log message does not contains the required error level string", Level.DEBUG, logLine2.getLevel());
            }
            ILoggingEvent logLine3 = getLog();
            assertTrue("The log message does not contains the required string", logLine3.getMessage().contains("[Export IBANs] - Persistence of IBANs extraction"));
            assertEquals("The log message does not contains the required error level string", Level.INFO, logLine3.getLevel());

        }
    }

    @SneakyThrows
    @Test
    void save_errorInSave() {

        ReflectionTestUtils.setField(dao, "ibansBatchSize", 10);

        try (MockedStatic<MongoClients> mockStatic = Mockito.mockStatic(MongoClients.class)) {

            // mocking components
            MongoClient client = mockMongoClientInit(mockStatic);
            when(collection.deleteOne(any(Bson.class))).thenAnswer(invocation -> { throw new MongoException("Mocked error!");});

            // executing logic to be checked
            dao.init();
            dao.save(getBrokerIbansEntity(5));
            dao.close();

            // check assertions
            verify(session, times(1)).startTransaction(any(TransactionOptions.class));
            verify(session, times(0)).commitTransaction();
            verify(session, times(1)).abortTransaction();
            verify(session, times(1)).close();
            verify(client, times(1)).close();
            verify(collection, times(1)).deleteOne(any(Bson.class));
            verify(collection, times(0)).insertOne(any(BrokerIbansEntity.class));
            ILoggingEvent logLine1 = getLog();
            assertTrue("The log message does not contains the required string", logLine1.getMessage().contains("[Export IBANs] - Persisting"));
            assertEquals("The log message does not contains the required error level string", Level.DEBUG, logLine1.getLevel());
            ILoggingEvent logLine2 = getLog();
            assertTrue("The log message does not contains the required string", logLine2.getMessage().contains("[Export IBANs] - An error occurred"));
            assertEquals("The log message does not contains the required error level string", Level.ERROR, logLine2.getLevel());
            ILoggingEvent logLine3 = getLog();
            assertTrue("The log message does not contains the required string", logLine3.getMessage().contains("[Export IBANs] - Persistence of IBANs extraction"));
            assertEquals("The log message does not contains the required error level string", Level.INFO, logLine3.getLevel());
        }
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked","rawtypes"})
    @ParameterizedTest
    @CsvSource({
            "false",
            "true"
    })
    void getAllBrokerCodeGreaterThan_ok(String gotEntities) {

        boolean hasEntities = Boolean.parseBoolean(gotEntities);

        try (MockedStatic<MongoClients> mockStatic = Mockito.mockStatic(MongoClients.class)) {

            // mocking components
            MongoClient client = mockMongoClientInit(mockStatic);
            MongoCursor cursor = mock(MongoCursor.class);
            if (hasEntities) {
                when(cursor.hasNext()).thenReturn(true, true, true, false);
                when(cursor.next()).thenReturn(BrokerIbansEntity.builder()
                                .brokerCode("0000000001")
                                .build())
                        .thenReturn(BrokerIbansEntity.builder()
                                .brokerCode("0000000002")
                                .build())
                        .thenReturn(BrokerIbansEntity.builder()
                                .brokerCode("0000000003")
                                .build());
            } else {
                when(cursor.hasNext()).thenReturn(false);
            }
            FindIterable mockedFindIterable = spy(FindIterable.class);
            when(mockedFindIterable.iterator()).thenReturn(cursor);
            when(collection.find(any(Bson.class))).thenReturn(mockedFindIterable);
            when(mockedFindIterable.projection(any(Bson.class))).thenReturn(mockedFindIterable);

            // executing logic to be checked
            dao.init();
            Set<String> result = dao.getAllBrokerCodeGreaterThan(new Date());
            dao.close();

            // check assertions
            verify(client, times(1)).close();
            if (hasEntities) {
                assertEquals("The result size is different from 3 elements", 3, result.size());
                assertTrue("The result set does not contains code", result.contains("0000000001"));
            } else {
                assertEquals("The result size is different from 0 elements", 0, result.size());
                assertFalse("The result set contains code", result.contains("0000000001"));
            }
        }
    }

    @SneakyThrows
    @Test
    void clean_ok() {

        try (MockedStatic<MongoClients> mockStatic = Mockito.mockStatic(MongoClients.class)) {

            // mocking components
            MongoClient client = mockMongoClientInit(mockStatic);

            // executing logic to be checked
            dao.init();
            dao.clean(new Date());
            dao.close();

            // check assertions
            verify(session, times(1)).startTransaction(any(TransactionOptions.class));
            verify(session, times(1)).commitTransaction();
            verify(session, times(0)).abortTransaction();
            verify(session, times(1)).close();
            verify(client, times(1)).close();
            verify(collection, times(1)).deleteMany(any(Bson.class));
            ILoggingEvent logLine1 = getLog();
            assertTrue("The log message does not contains the required string", logLine1.getMessage().contains("[Export IBANs] - Cleaning all extractions older than"));
            assertEquals("The log message does not contains the required error level string", Level.DEBUG, logLine1.getLevel());
            ILoggingEvent logLine2 = getLog();
            assertTrue("The log message does not contains the required string", logLine2.getMessage().contains("[Export IBANs] - Cleaning of all extractions"));
            assertEquals("The log message does not contains the required error level string", Level.DEBUG, logLine2.getLevel());

        }
    }


    @SneakyThrows
    @Test
    void clean_errorOnDeleteMany() {

        try (MockedStatic<MongoClients> mockStatic = Mockito.mockStatic(MongoClients.class)) {

            // mocking components
            MongoClient client = mockMongoClientInit(mockStatic);
            when(collection.deleteMany(any(Bson.class))).thenAnswer(invocation -> { throw new MongoException("Mocked error!");});

            // executing logic to be checked
            dao.init();
            dao.clean(new Date());
            dao.close();

            // check assertions
            verify(session, times(1)).startTransaction(any(TransactionOptions.class));
            verify(session, times(0)).commitTransaction();
            verify(session, times(1)).abortTransaction();
            verify(session, times(1)).close();
            verify(client, times(1)).close();
            verify(collection, times(1)).deleteMany(any(Bson.class));
            ILoggingEvent logLine1 = getLog();
            assertTrue("The log message does not contains the required string", logLine1.getMessage().contains("[Export IBANs] - Cleaning all extractions older than"));
            assertEquals("The log message does not contains the required error level string", Level.DEBUG, logLine1.getLevel());
            ILoggingEvent logLine2 = getLog();
            assertTrue("The log message does not contains the required string", logLine2.getMessage().contains("[Export IBANs] - An error occurred while cleaning"));
            assertEquals("The log message does not contains the required error level string", Level.ERROR, logLine2.getLevel());
            ILoggingEvent logLine3 = getLog();
            assertTrue("The log message does not contains the required string", logLine3.getMessage().contains("[Export IBANs] - Cleaning of all extractions"));
            assertEquals("The log message does not contains the required error level string", Level.DEBUG, logLine3.getLevel());

        }
    }


    private ILoggingEvent getLog() {
        List<ILoggingEvent> logsList = listAppender.list;
        ILoggingEvent log = logsList.get(0);
        logsList.remove(0);
        return log;
    }

    private ListAppender<ILoggingEvent> getMockLoggerListAppender() {
        Logger mockLogger = (Logger) LoggerFactory.getLogger(TransactionalBulkDAO.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        mockLogger.addAppender(listAppender);
        return listAppender;
    }


    private MongoClient mockMongoClientInit(MockedStatic<MongoClients> mockStatic) {
        MongoClient client = spy(MongoClient.class);
        mockStatic.when(() -> MongoClients.create(any(MongoClientSettings.class))).thenReturn(client);
        when(client.getDatabase(anyString())).thenReturn(db);
        when(db.getCollection("brokerIbans", BrokerIbansEntity.class)).thenReturn(collection);
        when(client.startSession()).thenReturn(session);
        return client;
    }

    private BrokerIbansEntity getBrokerIbansEntity(int numberOfIbans) {
        String creditorInstitution = "00000000000";
        List<BrokerIbanEntity> brokerIbanEntities = List.of();
        if (numberOfIbans > 0) {
            brokerIbanEntities = IntStream.rangeClosed(1, numberOfIbans)
                    .mapToObj(id -> BrokerIbanEntity.builder()
                            .ciName(creditorInstitution + " PA")
                            .ciFiscalCode(creditorInstitution)
                            .iban("IT00X" + creditorInstitution)
                            .status("ATTIVO")
                            .validityDate(Instant.now())
                            .description("description")
                            .label("label1")
                            .build())
                    .collect(Collectors.toList());
        }
        return BrokerIbansEntity.builder()
                .id(UUID.randomUUID().toString())
                .brokerCode("012345678900")
                .createdAt(Instant.now())
                .ibans(brokerIbanEntities)
                .build();
    }
}
