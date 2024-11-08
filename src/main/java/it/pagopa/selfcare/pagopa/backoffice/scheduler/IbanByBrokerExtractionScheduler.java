package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.CreditorInstitutionIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionView;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.CreditorInstitutionsIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.NumberOfTotalPagesSearch;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.NumberOfTotalPagesSearchWithListParam;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.PaginatedSearch;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.PaginatedSearchWithListParam;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCError;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForEndExecution;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForStartExecution;

@Slf4j
@Component
public class IbanByBrokerExtractionScheduler {

    private ApiConfigClient apiConfigClient;

    private ApiConfigSelfcareIntegrationClient apiConfigSCIntClient;

    private final AllPages allPages;

    private final BrokerIbansRepository brokerIbansRepository;

    private final CreditorInstitutionsIbansRepository creditorInstitutionsIbansRepository;

    private final ModelMapper modelMapper;

    private Integer getIbansPageLimit;

    private Integer getCIByBrokerPageLimit;

    private final Integer olderThanDays;

    private final Integer exportAgainAfterHours;

    private final boolean avoidExportPagoPABroker;

    @Autowired
    public IbanByBrokerExtractionScheduler(
            ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSCIntClient,
            AllPages allPages,
            BrokerIbansRepository brokerIbansRepository,
            CreditorInstitutionsIbansRepository creditorInstitutionsIbansRepository,
            ModelMapper modelMapper,
            @Value("${extraction.ibans.getIbans.pageLimit}") Integer getIbansPageLimit,
            @Value("${extraction.ibans.getCIByBroker.pageLimit}") Integer getCIByBrokerPageLimit,
            @Value("${extraction.ibans.clean.olderThanDays}") Integer olderThanDays,
            @Value("${extraction.ibans.exportAgainAfterHours}") Integer exportAgainAfterHours,
            @Value("${extraction.ibans.avoidExportPagoPABroker}") boolean avoidExportPagoPABroker
    ) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSCIntClient = apiConfigSCIntClient;
        this.allPages = allPages;
        this.brokerIbansRepository = brokerIbansRepository;
        this.creditorInstitutionsIbansRepository = creditorInstitutionsIbansRepository;
        this.modelMapper = modelMapper;
        this.getIbansPageLimit = getIbansPageLimit;
        this.getCIByBrokerPageLimit = getCIByBrokerPageLimit;
        this.olderThanDays = olderThanDays;
        this.exportAgainAfterHours = exportAgainAfterHours;
        this.avoidExportPagoPABroker = avoidExportPagoPABroker;
    }

    @Scheduled(cron = "${cron.job.schedule.expression.iban-export}")
    @SchedulerLock(name = "brokerIbansExport", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    @Transactional
    public void extract() {
        updateMDCForStartExecution("brokerIbansExport", "");
        log.info("[Export IBANs] - Starting IBAN extraction process...");

        // get all brokers registered in pagoPA platform
        Set<String> allBrokers = getAllBrokers();

        int brokerIndex = 0;
        List<String> failedBrokers = new ArrayList<>();
        // retrieve and save all IBANs for all CIs delegated by retrieved brokers
        for (String brokerCode : allBrokers) {
            long brokerExportStartTime = Calendar.getInstance().getTimeInMillis();
            log.info("[Export IBANs] - [{}/{}] Process broker with code [{}]...", ++brokerIndex, allBrokers.size(), brokerCode);
            try {
                upsertIbanForCIsDelegatedByBroker(brokerCode);
            } catch (Exception e) {
                log.warn("[Export IBANs] - An error occurred while updating IBANs for CI associated to broker [{}]: the extraction will not be updated for this broker!",
                        brokerCode, e);
                failedBrokers.add(brokerCode);
            }
            log.info("[Export IBANs] - Process of broker with code [{}] completed in [{}] ms!.", brokerCode, Utility.getTimelapse(brokerExportStartTime));
        }

        // clean files older than N days
        this.brokerIbansRepository.deleteAllByCreatedAtBefore(Instant.now().minus(this.olderThanDays, ChronoUnit.DAYS));
        if (failedBrokers.isEmpty()) {
            updateMDCForEndExecution();
            log.info("[Export IBANs] - IBAN extraction completed successfully");
        } else {
            updateMDCError("Export Broker IBAN");
            log.error("[Export IBANs] - Error during brokerIbansExport, process partially completed, the following brokers were not extracted/updated successfully: {}", failedBrokers);
        }
        MDC.clear();
    }

    private Set<String> getAllBrokers() {
        log.debug("[Export IBANs] - Retrieving the list of all brokers...");
        long startTime = Calendar.getInstance().getTimeInMillis();

        // retrieved the list of all brokers in pagoPA platform
        Set<String> brokerCodes = this.allPages.getAllBrokers();
        int totalRetrievedBrokerCodes = brokerCodes.size();

        // exclude all brokers which export was executed not too much time ago
        Set<String> brokerCodeToBeExcluded =
                this.brokerIbansRepository.findProjectedByCreatedAtGreaterThen(Instant.now().minus(this.exportAgainAfterHours, ChronoUnit.HOURS));
        if (this.avoidExportPagoPABroker) {
            brokerCodeToBeExcluded.add(Constants.PAGOPA_BROKER_CODE);
        }
        brokerCodes.removeAll(brokerCodeToBeExcluded);

        log.debug("[Export IBANs] - Excluded [{}}] of [{}] brokers because they were recently exported or are excluded a priori.",
                brokerCodeToBeExcluded.size(), totalRetrievedBrokerCodes);
        log.debug("[Export IBANs] - Retrieve of brokers completed successfully! Extracted [{}] broker codes in [{}] ms.",
                brokerCodes.size(), Utility.getTimelapse(startTime));
        return brokerCodes;
    }

    private void upsertIbanForCIsDelegatedByBroker(String brokerCode) {
        // gets all CIs delegated by broker
        Set<String> delegatedCITaxCodes = getDelegatedCreditorInstitutions(brokerCode);

        Set<IbanEntity> ibans = new HashSet<>();
        if (!delegatedCITaxCodes.isEmpty()) {
            // gets all IBANs related to the CIs
            ibans = getIban(delegatedCITaxCodes, brokerCode);

            // save all IBANs in creditorInstitutionIbans Collection in Mongo DB
            List<CreditorInstitutionIbansEntity> ibanEntities = ibans.parallelStream()
                    .map(elem -> this.modelMapper.map(elem, CreditorInstitutionIbansEntity.class))
                    .toList();
            this.creditorInstitutionsIbansRepository.saveAll(ibanEntities);
            log.debug("[Export IBANs] - Upsert completed of a batch of {} IBANs in Creditor-Institution Collection", ibanEntities.size());
        }
        // map retrieved data into new entity
        BrokerIbansEntity ibanEntity = BrokerIbansEntity.builder()
                .brokerCode(brokerCode)
                .createdAt(Instant.now())
                .ibans(new ArrayList<>(ibans))
                .build();
        this.brokerIbansRepository.findByBrokerCode(brokerCode).ifPresent(this.brokerIbansRepository::delete);
        this.brokerIbansRepository.save(ibanEntity);
    }

    private Set<String> getDelegatedCreditorInstitutions(String brokerCode) {
        log.debug("[Export IBANs] - Retrieving the list of all creditor institutions associated to broker [{}}]...", brokerCode);
        long startTime = Calendar.getInstance().getTimeInMillis();

        Set<String> delegatedCreditorInstitutions = this.allPages.executeParallelClientCalls(
                this.getCIsByBrokerCallback,
                this.getNumberOfCIsByBrokerCallback,
                CreditorInstitutionsView::getCreditorInstitutionList,
                CreditorInstitutionView::getIdDominio,
                this.getCIByBrokerPageLimit, brokerCode
        );

        log.debug("[Export IBANs] - Retrieve of creditor institutions associated to broker [{}] completed successfully! Extracted [{}] creditor institutions in [{}] ms.",
                brokerCode, delegatedCreditorInstitutions.size(), Utility.getTimelapse(startTime));
        return delegatedCreditorInstitutions;
    }

    private Set<IbanEntity> getIban(Set<String> ciCodes, String brokerCode) {
        log.debug("[Export IBANs] - Retrieving the list of all IBANs for [{}] creditor institutions related to broker [{}]...", ciCodes.size(), brokerCode);
        long startTime = Calendar.getInstance().getTimeInMillis();
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        ArrayList<String> filterCodes = new ArrayList<>(ciCodes);

        int numberOfPages = this.getNumberOfIbansByBrokerPagesCallback.search(1, 0, filterCodes);

        // create parallel calls
        List<CompletableFuture<Set<IbanEntity>>> futures = new LinkedList<>();
        CompletableFuture<Set<IbanEntity>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> this.getIbansByBrokerCallback.search(this.getIbansPageLimit, page, filterCodes))
                    .flatMap(response -> response.getIbans().stream())
                    .map(iban -> this.modelMapper.map(iban, IbanEntity.class))
                    .collect(Collectors.toSet());
        });
        futures.add(future);

        // join parallel calls
        Set<IbanEntity> brokerIbanEntities = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(e -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
                .join();

        log.info("[Export IBANs] - Retrieve of IBANs completed successfully! Extracted [{}] IBANs in [{}] ms.", brokerIbanEntities.size(), Utility.getTimelapse(startTime));
        return brokerIbanEntities;
    }

    private final PaginatedSearchWithListParam<IbansList> getIbansByBrokerCallback = (int limit, int page, List<String> codes) ->
            this.apiConfigSCIntClient.getIbans(limit, page, codes);

    private final PaginatedSearch<CreditorInstitutionsView> getCIsByBrokerCallback = (int limit, int page, String code) ->
            this.apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(limit, page, null, code, null, true, null, null, null, null);

    private final NumberOfTotalPagesSearchWithListParam getNumberOfIbansByBrokerPagesCallback = (int limit, int page, List<String> codes) -> {
        IbansList response = this.apiConfigSCIntClient.getIbans(limit, page, codes);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / this.getIbansPageLimit);
    };

    private final NumberOfTotalPagesSearch getNumberOfCIsByBrokerCallback = (int limit, int page, String code) -> {
        CreditorInstitutionsView response = this.apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(limit, page, null, code, null, true, null, null, null, null);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / this.getCIByBrokerPageLimit);
    };
}
