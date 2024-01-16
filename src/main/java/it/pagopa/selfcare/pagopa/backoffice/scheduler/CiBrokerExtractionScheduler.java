package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.*;

public class CiBrokerExtractionScheduler {

    @Scheduled(cron = "${cron.job.schedule.expression.iban-export}")
    @SchedulerLock(name = "brokerCiExport", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    public void extract() throws IOException {
        updateMDCForStartExecution();
        Set<String> allBrokers = getAllBrokers();
        for (String brokerCode : allBrokers) {

        }
        Calendar olderThan = Calendar.getInstance();
        olderThan.add(Calendar.DAY_OF_MONTH, olderThanDays * (-1));
        this.dao.clean(olderThan.getTime());
        this.dao.close();
        long timelapse = Utility.getTimelapse(startTime);
        updateMDCForEndExecution(timelapse);
        MDC.clear();
    }

    private void updateMDCForStartExecution() {
        MDC.put(METHOD, "brokerCiExport");
        MDC.put(START_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
    }


    private void updateMDCForEndExecution(long timelapse) {
        MDC.put(STATUS, "OK");
        MDC.put(CODE, "201");
        MDC.put(RESPONSE_TIME, String.valueOf(timelapse));
    }
}
