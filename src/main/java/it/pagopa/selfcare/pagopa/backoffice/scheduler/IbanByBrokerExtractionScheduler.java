package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.CreditorInstitutionIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.IbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionView;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import it.pagopa.selfcare.pagopa.backoffice.repository.CreditorInstitutionsIbansRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.TransactionalBulkDAO;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.MapInRequiredClass;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.NumberOfTotalPagesSearch;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.PaginatedSearch;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.*;


@Slf4j
@Component
public class IbanByBrokerExtractionScheduler {

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private ApiConfigSelfcareIntegrationClient apiConfigSCIntClient;

    @Autowired
    private AllPages allPages;

    @Autowired
    private TransactionalBulkDAO dao;

    @Autowired
    private CreditorInstitutionsIbansRepository creditorInstitutionsIbansRepository;

    @Value("${extraction.ibans.getBrokers.pageLimit}")
    private Integer getBrokersPageLimit;

    @Value("${extraction.ibans.getIbans.pageLimit}")
    private Integer getIbansPageLimit;

    @Value("${extraction.ibans.getCIByBroker.pageLimit}")
    private Integer getCIByBrokerPageLimit;

    @Value("${extraction.ibans.clean.olderThanDays}")
    private Integer olderThanDays;

    @Value("${extraction.ibans.exportAgainAfterHours}")
    private Integer exportAgainAfterHours;

    @Value("${extraction.ibans.avoidExportPagoPABroker}")
    private boolean avoidExportPagoPABroker;


    private final PaginatedSearch<Brokers> getBrokerECCallback = (int limit, int page, String code) ->
            apiConfigClient.getBrokersEC(limit, page, code, null, null, null);

    private final PaginatedSearch<IbansList> getIbansByBrokerCallback = (int limit, int page, String code) -> {
        List<String> codes = List.of(code.split(","));
        return apiConfigSCIntClient.getIbans(limit, page, codes);
    };

    private final PaginatedSearch<CreditorInstitutionsView> getCIsByBrokerCallback = (int limit, int page, String code) ->
            apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(limit, page, null, code, null, true, null, null, null, null);

    private final NumberOfTotalPagesSearch getNumberOfBrokerECPagesCallback = (int limit, int page, String code) -> {
        Brokers response = apiConfigClient.getBrokersEC(limit, page, null, null, null, null);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / getBrokersPageLimit);
    };

    private final NumberOfTotalPagesSearch getNumberOfIbansByBrokerPagesCallback = (int limit, int page, String code) -> {
        List<String> codes = List.of(code.split(","));
        IbansList response = apiConfigSCIntClient.getIbans(limit, page, codes);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / getIbansPageLimit);
    };

    private final NumberOfTotalPagesSearch getNumberOfCIsByBrokerCallback = (int limit, int page, String code) -> {
        CreditorInstitutionsView response = apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(limit, page, null, code, null, true, null, null, null, null);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / getCIByBrokerPageLimit);
    };

    private final MapInRequiredClass<IbanDetails, IbanEntity> convertIbanDetailsToBrokerIbanEntity = (IbanDetails elem) ->
            IbanEntity.builder()
                    .ciName(elem.getCiName())
                    .ciFiscalCode(elem.getCiFiscalCode())
                    .iban(elem.getIban())
                    .status(OffsetDateTime.now().isAfter(elem.getValidityDate()) ? "ATTIVO" : "DISATTIVO")
                    .validityDate(elem.getValidityDate().toInstant())
                    .description(elem.getDescription())
                    .label(elem.getLabels().stream()
                            .map(IbanLabel::getName)
                            .collect(Collectors.joining(" - ")))
                    .build();

    @Scheduled(cron = "${cron.job.schedule.expression.iban-export}")
    @SchedulerLock(name = "brokerIbansExport", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    public void extract() throws IOException {
        log.info("[Export IBANs] - Starting IBAN extraction process...");
        // log process start
        this.dao.init();
        long startTime = Calendar.getInstance().getTimeInMillis();
        updateMDCForStartExecution(startTime);
        // get all brokers registered in pagoPA platform
        Set<String> allBrokers = getAllBrokers();
        int numberOfRetrievedBrokers = allBrokers.size();
        int brokerIndex = 0;
        // retrieve and save all IBANs for all CIs delegated by retrieved brokers
        for (String brokerCode : allBrokers) {
            long brokerExportStartTime = Calendar.getInstance().getTimeInMillis();
            log.info(String.format("[Export IBANs] - [%d/%d] Analyzing broker with code [%s]...", ++brokerIndex, numberOfRetrievedBrokers, brokerCode));
            Optional<BrokerIbansEntity> brokerIbansEntity = getIbanForCIsDelegatedByBroker(brokerCode);
            brokerIbansEntity.ifPresent(this.dao::save);
            log.info(String.format("[Export IBANs] - Analysis of broker with code [%s] completed in [%d] ms!.", brokerCode, Utility.getTimelapse(brokerExportStartTime)));
        }
        // clean files older than N days
        Calendar olderThan = Calendar.getInstance();
        olderThan.add(Calendar.DAY_OF_MONTH, olderThanDays * (-1));
        this.dao.clean(olderThan.getTime());
        this.dao.close();
        // log process end
        long timelapse = Utility.getTimelapse(startTime);
        updateMDCForEndExecution(timelapse);
        log.info(String.format("[Export IBANs] - IBAN extraction completed successfully in [%d] ms!.", timelapse));
        MDC.clear();
    }

    private Set<String> getAllBrokers() {
        log.debug("[Export IBANs] - Retrieving the list of all brokers...");
        long startTime = Calendar.getInstance().getTimeInMillis();

        // retrieved the list of all brokers in pagoPA platform
        Set<String> brokerCodes = allPages.executeParallelClientCalls(getBrokerECCallback, getNumberOfBrokerECPagesCallback,
                Brokers::getBrokerList, Broker::getBrokerCode,
                getBrokersPageLimit, null);
        int totalRetrievedBrokerCodes = brokerCodes.size();
        // exclude all brokers which export was executed not too much time ago
        Calendar olderThan = Calendar.getInstance();
        olderThan.add(Calendar.HOUR, exportAgainAfterHours * (-1));
        Set<String> brokerCodeToBeExcluded = dao.getAllBrokerCodeGreaterThan(olderThan.getTime());
        if(avoidExportPagoPABroker) {
            brokerCodeToBeExcluded.add(Constants.PAGOPA_BROKER_CODE);
        }
        brokerCodes.removeAll(brokerCodeToBeExcluded);
        log.debug(String.format("[Export IBANs] - Excluded [%d] of [%d] brokers because they were recently exported or are excluded a priori.", brokerCodeToBeExcluded.size(), totalRetrievedBrokerCodes));

        log.info(String.format("[Export IBANs] - Retrieve of brokers completed successfully! Extracted [%d] broker codes in [%d] ms.", brokerCodes.size(), Utility.getTimelapse(startTime)));
        return brokerCodes;
    }

    private Optional<BrokerIbansEntity> getIbanForCIsDelegatedByBroker(String brokerCode) {
        Optional<BrokerIbansEntity> brokerIbansEntity;
        try {
            // gets all CIs delegated by broker
            Set<String> delegatedCITaxCodes = getDelegatedCreditorInstitutions(brokerCode);
            // gets all IBANs related to the CIs
            Set<IbanEntity> ibans = getIbans(delegatedCITaxCodes, brokerCode);

            // save all IBANs in creditorInstitutionIbans Collection in Mongo DB
            List<CreditorInstitutionIbansEntity> ibanEntities = ibans.stream()
                    .map(elem -> (CreditorInstitutionIbansEntity) CreditorInstitutionIbansEntity.builder()
                            .id(elem.getIban())
                            .iban(elem.getIban())
                            .label(elem.getLabel())
                            .ciName(elem.getCiName())
                            .status(elem.getStatus())
                            .validityDate(elem.getValidityDate())
                            .description(elem.getDescription())
                            .ciFiscalCode(elem.getCiFiscalCode())
                            .build())
                    .toList();
            creditorInstitutionsIbansRepository.saveAll(ibanEntities);
            log.info("[Export IBANs] - Upsert completed of a batch of {} IBANs in Creditor-Institution Collection", ibanEntities.size());

            // map retrieved data into new entity
            brokerIbansEntity = Optional.of(BrokerIbansEntity.builder()
                    .brokerCode(brokerCode)
                    .createdAt(Instant.now())
                    .ibans(new ArrayList<>(ibans))
                    .build());
        } catch (Exception e) {
            log.warn(String.format("[Export IBANs] - An error occurred while retrieving IBANs for CI associated to broker [%s]: the extraction will not be updated for this broker! Exception:", brokerCode), e);
            brokerIbansEntity = Optional.empty();
        }
        return brokerIbansEntity;
    }

    private Set<String> getDelegatedCreditorInstitutions(String brokerCode) {
        log.debug(String.format("[Export IBANs] - Retrieving the list of all creditor institutions associated to broker [%s]...", brokerCode));
        long startTime = Calendar.getInstance().getTimeInMillis();

        Set<String> delegatedCreditorInstitutions = allPages.executeParallelClientCalls(getCIsByBrokerCallback, getNumberOfCIsByBrokerCallback,
                CreditorInstitutionsView::getCreditorInstitutionList, CreditorInstitutionView::getIdDominio,
                getCIByBrokerPageLimit, brokerCode);

        log.info(String.format("[Export IBANs] - Retrieve of creditor institutions associated to broker [%s] completed successfully! Extracted [%d] creditor institutions in [%d] ms.", brokerCode, delegatedCreditorInstitutions.size(), Utility.getTimelapse(startTime)));
        return delegatedCreditorInstitutions;
    }

    private Set<IbanEntity> getIbans(Set<String> ciCodes, String brokerCode) {
        Set<IbanEntity> brokerIbanEntities = new HashSet<>();
        if(!ciCodes.isEmpty()) {
            log.debug(String.format("[Export IBANs] - Retrieving the list of all IBANs for [%d] creditor institutions related to broker [%s]...", ciCodes.size(), brokerCode));
            long startTime = Calendar.getInstance().getTimeInMillis();

            int limit = 100;
            int totalSize = ciCodes.size();
            List<String> ciCodesAsList = new ArrayList<>(ciCodes);
            for (int i = 0; i < totalSize; i += limit) {
                List<String> partition = ciCodesAsList.subList(i, Math.min(i + limit, totalSize));
                String stringifiedCiCodesPartition = String.join(",", partition);
                Set<IbanEntity> partitionedBrokerIbanEntities = allPages.executeParallelClientCalls(getIbansByBrokerCallback, getNumberOfIbansByBrokerPagesCallback,
                        IbansList::getIbans, convertIbanDetailsToBrokerIbanEntity,
                        getIbansPageLimit, stringifiedCiCodesPartition);
                brokerIbanEntities.addAll(partitionedBrokerIbanEntities);
            }

            log.info(String.format("[Export IBANs] - Retrieve of IBANs completed successfully! Extracted [%d] IBANs in [%d] ms.", brokerIbanEntities.size(), Utility.getTimelapse(startTime)));
        } else {
            log.info(String.format("[Export IBANs] - No creditor institution related to broker [%s] was found. Skipping it!", brokerCode));
        }
        return brokerIbanEntities;
    }


    private void updateMDCForStartExecution(long startTime) {
        MDC.put(METHOD, "brokerIbansExport");
        MDC.put(START_TIME, String.valueOf(startTime));
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(OPERATION_ID, UUID.randomUUID().toString());
        MDC.put(ARGS, "");
    }

    private void updateMDCForEndExecution(long timelapse) {
        MDC.put(STATUS, "OK");
        MDC.put(CODE, "201");
        MDC.put(RESPONSE_TIME, String.valueOf(timelapse));
    }

}
