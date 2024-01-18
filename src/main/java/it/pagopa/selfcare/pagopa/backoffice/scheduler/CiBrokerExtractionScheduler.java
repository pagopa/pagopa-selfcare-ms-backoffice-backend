package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.*;

@Component
public class CiBrokerExtractionScheduler {

    @Autowired
    private AllPages allPages;

    @Scheduled(cron = "${cron.job.schedule.expression.ci-export}")
    @SchedulerLock(name = "brokerCiExport", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    public void extract() throws IOException {
        updateMDCForStartExecution();
        Set<String> allBrokers = allPages.getAllBrokers();
        for (String brokerCode : allBrokers) {
            var entities = allPages.getCreditorInstitutionsAssociatedToBroker(brokerCode);

        }

        updateMDCForEndExecution();
        MDC.clear();
    }

    private void updateMDCForStartExecution() {
        MDC.put(METHOD, "brokerCiExport");
        MDC.put(START_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
    }


    private void updateMDCForEndExecution() {
        MDC.put(STATUS, "OK");
        MDC.put(CODE, "201");
        MDC.put(RESPONSE_TIME, getExecutionTime());
    }


}
