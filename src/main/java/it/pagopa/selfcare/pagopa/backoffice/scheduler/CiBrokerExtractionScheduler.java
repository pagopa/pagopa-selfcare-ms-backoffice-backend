package it.pagopa.selfcare.pagopa.backoffice.scheduler;

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

import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCError;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForEndExecution;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForStartExecution;

@Component
@Slf4j
public class CiBrokerExtractionScheduler {

    private final AllPages allPages;

    private final BrokerInstitutionsRepository brokerInstitutionsRepository;

    private final Integer olderThanDays;

    @Autowired
    public CiBrokerExtractionScheduler(
            AllPages allPages,
            BrokerInstitutionsRepository brokerInstitutionsRepository,
            @Value("${extraction.ibans.clean.olderThanDays}") Integer olderThanDays
    ) {
        this.allPages = allPages;
        this.brokerInstitutionsRepository = brokerInstitutionsRepository;
        this.olderThanDays = olderThanDays;
    }

    @Scheduled(cron = "${cron.job.schedule.expression.ci-export}")
    @SchedulerLock(name = "brokerCiExport", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    @Transactional
    public void extractCI() {
        updateMDCForStartExecution("brokerCiExport", "");
        log.info("[Export-CI] export starting...");
        Set<String> allBrokers = this.allPages.getAllBrokers();

        int index = 0;
        boolean extractionSuccess = true;
        for (String brokerCode : allBrokers) {
            log.debug("[Export-CI] analyzing broker {} ({}/{})", brokerCode, index++, allBrokers.size());
            try {
                this.allPages.upsertCreditorInstitutionsAssociatedToBroker(brokerCode);
            } catch (Exception e) {
                log.warn("[Export-CI] An error occurred while updating CI associated to broker [{}]: the extraction will not be updated for this broker!",
                        brokerCode, e);
                extractionSuccess = false;
            }
        }

        // delete the old entities
        this.brokerInstitutionsRepository.deleteAllByCreatedAtBefore(Instant.now().minus(Duration.ofDays(olderThanDays)));

        if (extractionSuccess) {
            updateMDCForEndExecution();
            log.info("[Export-CI] export complete!");
        } else {
            updateMDCError("Export CI Broker");
            log.error("[Export-CI] An error occurred during the export creation, not all broker CI were extracted successfully");
        }
        MDC.clear();
    }
}
