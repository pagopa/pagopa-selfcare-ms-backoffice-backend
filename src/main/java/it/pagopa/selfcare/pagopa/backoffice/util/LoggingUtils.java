package it.pagopa.selfcare.pagopa.backoffice.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for logging
 */
@Component
public class LoggingUtils {

    public String checkLogParam(String logParam) {
        if (logParam.matches("\\w*")) {
            return logParam;
        }
        return "suspicious log param";
    }
}
