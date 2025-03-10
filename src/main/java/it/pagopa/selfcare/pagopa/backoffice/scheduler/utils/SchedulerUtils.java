package it.pagopa.selfcare.pagopa.backoffice.scheduler.utils;

import org.slf4j.MDC;

import java.util.Calendar;
import java.util.UUID;

import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.*;

public class SchedulerUtils {

    public static void updateMDCForStartExecution(String method, String args) {
        MDC.put(METHOD, method);
        MDC.put(START_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(OPERATION_ID, UUID.randomUUID().toString());
        MDC.put(ARGS, args);
    }

    public static void updateMDCForEndExecution() {
        MDC.put(STATUS, "OK");
        MDC.put(CODE, "201");
        MDC.put(RESPONSE_TIME, getExecutionTime());
    }

    public static void updateMDCError(Exception e, String method) {
        MDC.put(STATUS, "KO");
        MDC.put(CODE, "500");
        MDC.put(RESPONSE_TIME, getExecutionTime());
        MDC.put(FAULT_CODE, method);
        MDC.put(FAULT_DETAIL, e.getMessage());
    }

    public static void updateMDCError(String method) {
        MDC.put(STATUS, "KO");
        MDC.put(CODE, "500");
        MDC.put(RESPONSE_TIME, getExecutionTime());
        MDC.put(FAULT_CODE, method);
    }

    private SchedulerUtils() {
    }
}
