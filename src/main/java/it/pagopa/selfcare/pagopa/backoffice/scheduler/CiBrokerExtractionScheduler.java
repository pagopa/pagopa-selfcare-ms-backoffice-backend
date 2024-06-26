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
import java.util.Set;

import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.*;

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
    public void extractCi() {
        // just a start print
        updateMDCForStartExecution("brokerCiExport", "");
        log.info("[Export-CI] export starting...");
        try {
            Set<String> allBrokers = allPages.getAllBrokers();
            int index = 0;
            for (String brokerCode : allBrokers) {
                log.debug("[Export-CI] analyzing broker " + brokerCode + " (" + index++ + "/" + allBrokers.size() + ")");
                upsertBrokerInstitution(brokerCode);
            }

            // delete the old entities
            brokerInstitutionsRepository.deleteAllByCreatedAtBefore(Instant.now().minus(Duration.ofDays(olderThanDays)));
            // just a success print
            updateMDCForEndExecution();
            log.info("[Export-CI] export complete!");
        } catch (Exception e) {
            updateMDCError(e, "Export CI Broker");
            log.error("[Export-CI] an error occurred during the export creation", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    public void upsertBrokerInstitution(String brokerCode) {
        // delete old entity if it exists
        log.debug("[Export-CI] delete old table");
        brokerInstitutionsRepository.findByBrokerCode(brokerCode)
                .ifPresent(brokerInstitutionsRepository::delete);
        // retrieve new data
        log.debug("[Export-CI] retrieve new data for the broker {}", brokerCode);
        var institutions = allPages.getCreditorInstitutionsAssociatedToBroker(brokerCode).stream().toList();
        // build new entity
        var entity = BrokerInstitutionsEntity.builder()
                .brokerCode(brokerCode)
                .institutions(institutions)
                .build();
        // save new entity
        log.debug("[Export-CI] save " + institutions.size() + " items for the broker " + brokerCode);
        brokerInstitutionsRepository.save(entity);
    }


}
