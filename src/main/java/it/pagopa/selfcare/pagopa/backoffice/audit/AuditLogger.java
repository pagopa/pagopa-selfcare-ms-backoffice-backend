package it.pagopa.selfcare.pagopa.backoffice.audit;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    public void info(Logger logger, String format, Object... args) {
        try (var audit = AuditScope.enable()) {
            logger.info(format, args);
        }
    }

    public void warn(Logger logger, String format, Object... args) {
        try (var audit = AuditScope.enable()) {
            logger.warn(format, args);
        }
    }

    public void error(Logger logger, String format, Object... args) {
        try (var audit = AuditScope.enable()) {
            logger.error(format, args);
        }
    }

    public void error(Logger logger, String message, Throwable throwable) {
        try (var audit = AuditScope.enable()) {
            logger.error(message, throwable);
        }
    }
}