package it.pagopa.selfcare.pagopa.backoffice.audit;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class AuditLoggerTest {

    private AuditLogger auditLogger;
    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        auditLogger = new AuditLogger();

        logger = (Logger) LoggerFactory.getLogger(AuditLoggerTest.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void cleanup() {
        MDC.clear();
        logger.detachAppender(listAppender);
    }

    @Test
    void shouldAddAuditFlagToMDCDuringLogging() {
        String message = "Test audit message";

        auditLogger.info(logger, message);

        assertEquals(1, listAppender.list.size(), "Should have logged 1 message");

        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals(message, logEvent.getFormattedMessage());
        assertEquals("true", logEvent.getMDCPropertyMap().get("audit"),
                "Log event should have audit=true in MDC");

        assertNull(MDC.get("audit"), "MDC should be cleaned after logging");
    }

    @Test
    void shouldLogMultipleMessagesWithAudit() {
        auditLogger.info(logger, "First message");
        auditLogger.warn(logger, "Second message");
        auditLogger.error(logger, "Third message");

        assertEquals(3, listAppender.list.size());

        for (ILoggingEvent event : listAppender.list) {
            assertEquals("true", event.getMDCPropertyMap().get("audit"),
                    "All log events should have audit=true");
        }

        assertNull(MDC.get("audit"));
    }

    @Test
    void shouldPreserveOtherMDCValues() {
        MDC.put("requestId", "12345");
        MDC.put("userId", "user-1");

        auditLogger.info(logger, "Test message");

        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals("true", logEvent.getMDCPropertyMap().get("audit"));
        assertEquals("12345", logEvent.getMDCPropertyMap().get("requestId"));
        assertEquals("user-1", logEvent.getMDCPropertyMap().get("userId"));

        assertNull(MDC.get("audit"));
        assertEquals("12345", MDC.get("requestId"));
        assertEquals("user-1", MDC.get("userId"));
    }
}