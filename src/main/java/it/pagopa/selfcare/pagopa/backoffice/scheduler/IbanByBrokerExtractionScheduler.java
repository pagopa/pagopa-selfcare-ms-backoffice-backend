package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbanEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerIbansEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanLabel;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import it.pagopa.selfcare.pagopa.backoffice.repository.TransactionalBulkDAO;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.GetResultList;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.MapInRequiredClass;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.NumberOfTotalPagesSearch;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.PaginatedSearch;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
@Component
public class IbanByBrokerExtractionScheduler {

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private ApiConfigSelfcareIntegrationClient apiConfigSCIntClient;

    @Autowired
    private TransactionalBulkDAO dao;

    @Value("${extraction.ibans.getBrokers.pageLimit}")
    private Integer getBrokersPageLimit;

    @Value("${extraction.ibans.getBrokers.pageLimit}")
    private Integer getIbansPageLimit;

    private final PaginatedSearch<Brokers> getBrokerECCallback = (int limit, int page, String code) ->
            apiConfigClient.getBrokersEC(limit, page, code, null, null, null);

    private final PaginatedSearch<IbansList> getIbansByBrokerCallback = (int limit, int page, String code) -> {
        List<String> codes = List.of(code);
        return apiConfigSCIntClient.getIbans(limit, page, codes);
    };

    private final NumberOfTotalPagesSearch getNumberOfBrokerECPagesCallback = (int limit, int page, String code) -> {
        Brokers response = apiConfigClient.getBrokersEC(limit, page, null, null, null, null);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / limit);
    };

    private final NumberOfTotalPagesSearch getNumberOfIbansByBrokerPagesCallback = (int limit, int page, String code) -> {
        List<String> codes = List.of(code);
        IbansList response = apiConfigSCIntClient.getIbans(limit, page, codes);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / limit);
    };

    private final MapInRequiredClass<IbanDetails, BrokerIbanEntity> convertIbanDetailsToBrokerIbanEntity = (IbanDetails elem) ->
        BrokerIbanEntity.builder()
                .ciName(elem.getCiName())
                .ciFiscalCode(elem.getCiFiscalCode())
                .iban(elem.getIban())
                .status(OffsetDateTime.now().isBefore(elem.getDueDate()) ? "ATTIVO" : "DISATTIVO")
                .validityDate(elem.getValidityDate().toInstant())
                .description(elem.getDescription())
                .label(elem.getLabels().stream()
                        .map(IbanLabel::getName)
                        .collect(Collectors.joining(" - ")))
                .build();

    @Scheduled(cron = "${cron.job.schedule.expression.iban-export}")
    @Async
    public void extract() {
        log.info("[Export IBANs] - Starting IBAN extraction process...");
        long startTime = Calendar.getInstance().getTimeInMillis();
        Instant now = Instant.now();
        List<BrokerIbansEntity> entities = new LinkedList<>();
        for (String brokerCode : getAllBrokers()) {
            BrokerIbansEntity brokerIbansEntity = BrokerIbansEntity.builder()
                    .brokerCode(brokerCode)
                    .createdAt(now)
                    .ibans(getIbans(brokerCode))
                    .build();
            entities.add(brokerIbansEntity);
        }
        dao.saveAll(entities);
        log.info(String.format("[Export IBANs] - IBAN extraction completed successfully in [%d] ms!.", Utility.getTimelapse(startTime)));
    }

    private List<String> getAllBrokers() {
        log.debug("[Export IBANs] - Retrieving the list of all brokers...");
        long startTime = Calendar.getInstance().getTimeInMillis();

        List<String> brokerCodes = executeParallelClientCalls(getBrokerECCallback, getNumberOfBrokerECPagesCallback, Brokers::getBrokerList, Broker::getBrokerCode, getBrokersPageLimit, null);

        log.info(String.format("[Export IBANs] - Retrieve of brokers completed successfully! Extracted [%d] broker codes in [%d] ms.", brokerCodes.size(), Utility.getTimelapse(startTime)));
        return brokerCodes;
    }

    private List<BrokerIbanEntity> getIbans(String brokerCode) {
        log.debug(String.format("[Export IBANs] - Retrieving the list of all IBANs for broker [%s]...", brokerCode));
        long startTime = Calendar.getInstance().getTimeInMillis();

        List<BrokerIbanEntity> brokerIbanEntities = executeParallelClientCalls(getIbansByBrokerCallback, getNumberOfIbansByBrokerPagesCallback, IbansList::getIbans, convertIbanDetailsToBrokerIbanEntity, getIbansPageLimit, brokerCode);

        log.info(String.format("[Export IBANs] - Retrieve of IBANs completed successfully! Extracted [%d] IBANs in [%d] ms.", brokerIbanEntities.size(), Utility.getTimelapse(startTime)));
        return brokerIbanEntities;
    }

    /**
     * ...
     *
     * @param paginatedSearch
     * @param pageNumberSearch
     * @param getResultList
     * @param mapInRequiredClass
     * @param limit
     * @param filterCode
     * @return the list of object in type 'R', mapped by <code>mapInRequiredClass</code> callback.
     * @param <M> the main type retrieved from main search, i.e. the type that contains the list of results and the PageInfo detail
     * @param <N> the type of the nested object retreived from main search, i.e. the type related to the list of results
     * @param <R> the type of the final result list generated by the 'mapInRequiredClass' callback
     */
    private <M, N, R> List<R> executeParallelClientCalls(PaginatedSearch<M> paginatedSearch, NumberOfTotalPagesSearch pageNumberSearch,
                                                         GetResultList<M, N> getResultList, MapInRequiredClass<N, R> mapInRequiredClass,
                                                         int limit, String filterCode) {

        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        int numberOfPages = pageNumberSearch.search(1, 0, filterCode);

        List<CompletableFuture<List<R>>> futures = new LinkedList<>();
        CompletableFuture<List<R>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> paginatedSearch.search(limit, page, filterCode))
                    .flatMap(response -> getResultList.get(response).stream())
                    .map(mapInRequiredClass::map)
                    .collect(Collectors.toList());
        });
        futures.add(future);

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(e -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .join();
    }
}
