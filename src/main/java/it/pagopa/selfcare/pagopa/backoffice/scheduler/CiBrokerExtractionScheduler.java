package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.*;

@Component
@Slf4j
public class CiBrokerExtractionScheduler {

    @Autowired
    private AllPages allPages;

    @Autowired
    private BrokerInstitutionsRepository brokerInstitutionsRepository;

    @Value("${extraction.ibans.clean.olderThanDays}")
    private Integer olderThanDays;

    @Scheduled(cron = "${cron.job.schedule.expression.ci-export}")
    @SchedulerLock(name = "brokerCiExport", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    @Transactional
    public void extract() {
        updateMDCForStartExecution();
        log.info("[Export-CI] export starting...");

        Set<String> allBrokers = allPages.getAllBrokers();
        for (String brokerCode : allBrokers) {
            upsertBrokerInstitution(brokerCode);
        }

        brokerInstitutionsRepository.deleteAllByCreatedAtBefore(Instant.now().minus(Duration.ofDays(olderThanDays)));
        updateMDCForEndExecution();
        log.info("[Export-CI] export complete!");
        MDC.clear();
    }

    @Transactional
    public void upsertBrokerInstitution(String brokerCode) {
        brokerInstitutionsRepository.deleteByBrokerCode(brokerCode);
        var institutions = allPages.getCreditorInstitutionsAssociatedToBroker(brokerCode)
                .stream()
                .toList();
        var entity = BrokerInstitutionsEntity.builder()
                .brokerCode(brokerCode)
                .institutions(institutions)
                .build();
        brokerInstitutionsRepository.save(entity);
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
