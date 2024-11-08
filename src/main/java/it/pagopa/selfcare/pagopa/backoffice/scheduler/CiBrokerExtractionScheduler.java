package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.BrokerCreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperStationsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.NumberOfTotalPagesSearch;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.PaginatedSearch;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCError;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForEndExecution;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForStartExecution;

@Component
@Slf4j
public class CiBrokerExtractionScheduler {

    private final AllPages allPages;

    private final BrokerInstitutionsRepository brokerInstitutionsRepository;

    private ApiConfigSelfcareIntegrationClient apiConfigSCIntClient;

    private final WrapperStationsRepository wrapperStationsRepository;

    private Integer getCIByBrokerPageLimit;

    private final Integer olderThanDays;

    @Autowired
    public CiBrokerExtractionScheduler(
            AllPages allPages,
            BrokerInstitutionsRepository brokerInstitutionsRepository,
            ApiConfigSelfcareIntegrationClient apiConfigSCIntClient,
            WrapperStationsRepository wrapperStationsRepository,
            @Value("${extraction.ibans.getCIByBroker.pageLimit}") Integer getCIByBrokerPageLimit,
            @Value("${extraction.ibans.clean.olderThanDays}") Integer olderThanDays
    ) {
        this.allPages = allPages;
        this.brokerInstitutionsRepository = brokerInstitutionsRepository;
        this.apiConfigSCIntClient = apiConfigSCIntClient;
        this.wrapperStationsRepository = wrapperStationsRepository;
        this.getCIByBrokerPageLimit = getCIByBrokerPageLimit;
        this.olderThanDays = olderThanDays;
    }

    @Scheduled(cron = "${cron.job.schedule.expression.ci-export}")
    @SchedulerLock(name = "brokerCiExport", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    @Transactional
    public void extractCI() {
        updateMDCForStartExecution("brokerCiExport", "");
        log.info("[Export-CI] - Starting CI broker export...");
        Set<String> allBrokers = this.allPages.getAllBrokers();

        int index = 0;
        List<String> failedBrokers = new ArrayList<>();
        for (String brokerCode : allBrokers) {
            log.debug("[Export-CI] - Analyzing broker {} ({}/{})", brokerCode, index++, allBrokers.size());
            try {
                upsertCreditorInstitutionsAssociatedToBroker(brokerCode);
            } catch (Exception e) {
                log.warn("[Export-CI] - An error occurred while updating CI associated to broker [{}]: the extraction will not be updated for this broker!",
                        brokerCode, e);
                failedBrokers.add(brokerCode);
            }
        }

        // delete the old entities
        this.brokerInstitutionsRepository.deleteAllByCreatedAtBefore(Instant.now().minus(Duration.ofDays(olderThanDays)));

        if (failedBrokers.isEmpty()) {
            updateMDCForEndExecution();
            log.info("[Export-CI] - Export complete successfully!");
        } else {
            updateMDCError("Export CI Broker");
            log.error("[Export-CI] Error during brokerCiExport, process partially completed, the following brokers were not extracted/updated successfully: {}", failedBrokers);
        }
        MDC.clear();
    }

    private void upsertCreditorInstitutionsAssociatedToBroker(String brokerCode) {
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        int numberOfPages = this.getCreditorInstitutionsAssociatedToBrokerPages.search(1, 0, brokerCode);

        log.debug("[Export-CI] - Delete old document");
        this.brokerInstitutionsRepository.findByBrokerCode(brokerCode).ifPresent(this.brokerInstitutionsRepository::delete);
        log.debug("[Export-CI] - Create new document for broker {}", brokerCode);
        this.brokerInstitutionsRepository.save(BrokerInstitutionsEntity.builder().brokerCode(brokerCode).build());

        // create parallel calls
        log.debug("[Export-CI] - Retrieve new data for the broker {} and updates its document", brokerCode);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> this.getCreditorInstitutionsAssociatedToBroker.search(this.getCIByBrokerPageLimit, page, brokerCode))
                    .map(response -> response.getCreditorInstitutions().parallelStream()
                            .map(this::convertCreditorInstitutionDetailToBrokerInstitutionEntity)
                            .toList())
                    .forEach(institutions -> this.brokerInstitutionsRepository.updateBrokerInstitutionsList(brokerCode, institutions));
        });
        futures.add(future);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private BrokerInstitutionEntity convertCreditorInstitutionDetailToBrokerInstitutionEntity(CreditorInstitutionDetail ci) {
        Instant activationDate = null;
        var wrapper = this.wrapperStationsRepository.findByIdAndType(ci.getStationCode(), WrapperType.STATION);
        if (wrapper.isPresent() && wrapper.get().getEntities() != null && wrapper.get().getEntities().get(0) != null) {
            StationDetails station = (wrapper.get().getEntities().get(0)).getEntity();
            activationDate = station.getActivationDate();
        }

        return BrokerInstitutionEntity.builder()
                .companyName(ci.getBusinessName())
                .taxCode(ci.getCreditorInstitutionCode())
                .intermediated(!ci.getBrokerCode().equals(ci.getCreditorInstitutionCode()))
                .brokerCompanyName(ci.getBrokerBusinessName())
                .brokerTaxCode(ci.getBrokerCode())
                .model(3)
                .auxDigit(getAuxDigit(ci))
                .segregationCode(ci.getSegregationCode())
                .applicationCode(ci.getApplicationCode())
                .cbillCode(ci.getCbillCode())
                .stationId(ci.getStationCode())
                .stationState(ci.getStationEnabled() ? "ENABLED" : "DISABLED")
                .endpointRT(ci.getEndpointRT())
                .endpointRedirect(ci.getEndpointRedirect())
                .endpointMU(ci.getEndpointMU())
                .primitiveVersion(ci.getVersionePrimitive())
                .ciStatus(ci.getCiStatus())
                .activationDate(activationDate)
                .version(String.valueOf(ci.getStationVersion()))
                .broadcast(ci.getBroadcast())
                .pspPayment(ci.getPspPayment())
                .build();
    }

    private static String getAuxDigit(CreditorInstitutionDetail ci) {
        /* if aux digit is null we use this table to calculate it.

          aux | segregation | application
          0   |     null    |   value
          3   |    value    |    null
          0/3 |    value    |   value

        */
        if (ci.getAuxDigit() == null) {
            if (ci.getSegregationCode() == null && ci.getApplicationCode() != null) {
                return "0";
            }
            if (ci.getSegregationCode() != null && ci.getApplicationCode() == null) {
                return "3";
            }
            if (ci.getSegregationCode() != null && ci.getApplicationCode() != null) {
                return "0/3";
            }
            return "";

        } else {
            return String.valueOf(Math.toIntExact(ci.getAuxDigit()));
        }
    }

    private final PaginatedSearch<BrokerCreditorInstitutionDetails> getCreditorInstitutionsAssociatedToBroker = (int limit, int page, String code) ->
            this.apiConfigSCIntClient.getCreditorInstitutionsAssociatedToBroker(limit, page, true, code);

    private final NumberOfTotalPagesSearch getCreditorInstitutionsAssociatedToBrokerPages = (int limit, int page, String code) -> {
        var response = this.apiConfigSCIntClient.getCreditorInstitutionsAssociatedToBroker(limit, page, true, code);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / this.getCIByBrokerPageLimit);
    };
}
