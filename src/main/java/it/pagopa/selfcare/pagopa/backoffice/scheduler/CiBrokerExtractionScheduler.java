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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
    public void extractCi() {
        // just a start print
        updateMDCForStartExecution("brokerCiExport", "");
        log.info("[Export-CI] export starting...");
        try {
            Set<String> allBrokers = this.allPages.getAllBrokers();

            int index = 0;
            for (String brokerCode : allBrokers) {
                log.debug("[Export-CI] analyzing broker {} ({}/{})", brokerCode, index++, allBrokers.size());
                this.allPages.getCreditorInstitutionsAssociatedToBroker(brokerCode);
            }

            // delete the old entities
            this.brokerInstitutionsRepository.deleteAllByCreatedAtBefore(Instant.now().minus(Duration.ofDays(olderThanDays)));
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
}
