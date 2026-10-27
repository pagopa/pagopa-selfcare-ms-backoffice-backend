package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.CiBrokerExtractionScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Manual trigger of the scheduled jobs, meant only for testing purposes in non-production environments.
 * It is enabled only when cron.job.manual-trigger.enabled=true, so it is not included in the generated
 * OpenAPI and it is not exposed through APIM.
 */
@Slf4j
@RestController
@RequestMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(name = "cron.job.manual-trigger.enabled", havingValue = "true")
public class SchedulerController {

    private final CiBrokerExtractionScheduler ciBrokerExtractionScheduler;

    @Autowired
    public SchedulerController(CiBrokerExtractionScheduler ciBrokerExtractionScheduler) {
        this.ciBrokerExtractionScheduler = ciBrokerExtractionScheduler;
    }

    /**
     * Start asynchronously the extraction of the creditor institutions associated to each broker.
     * If the job is already running (or ran less than lockAtLeastFor ago) the execution is skipped by ShedLock.
     *
     * @return 202 Accepted, the job runs in background
     */
    @Operation(
            summary = "Trigger the creditor institutions broker extraction job",
            security = {@SecurityRequirement(name = "JWT")},
            tags = {"Jobs"}
    )
    @PostMapping(value = "/ci-broker-extraction")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Void> triggerCiBrokerExtraction() {
        log.info("[Export-CI] - Manual trigger requested");
        this.ciBrokerExtractionScheduler.extractCI();
        return ResponseEntity.accepted().build();
    }
}
