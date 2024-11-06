package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;


import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.BrokerCreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperStationsRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class AllPages {

    private ApiConfigClient apiConfigClient;

    private ApiConfigSelfcareIntegrationClient apiConfigSCIntClient;

    private final WrapperStationsRepository wrapperStationsRepository;

    private final BrokerInstitutionsRepository brokerInstitutionsRepository;

    private Integer getBrokersPageLimit;

    private final Integer getCIByBrokerPageLimit;

    @Autowired
    public AllPages(
            ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSCIntClient,
            WrapperStationsRepository wrapperStationsRepository,
            BrokerInstitutionsRepository brokerInstitutionsRepository,
            @Value("${extraction.ibans.getBrokers.pageLimit}") Integer getBrokersPageLimit,
            @Value("${extraction.ibans.getCIByBroker.pageLimit}") Integer getCIByBrokerPageLimit
    ) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSCIntClient = apiConfigSCIntClient;
        this.wrapperStationsRepository = wrapperStationsRepository;
        this.brokerInstitutionsRepository = brokerInstitutionsRepository;
        this.getBrokersPageLimit = getBrokersPageLimit;
        this.getCIByBrokerPageLimit = getCIByBrokerPageLimit;
    }

    /**
     * @return the set of all brokers in pagoPA platform
     */
    @Cacheable(value = "getAllBrokers")
    public Set<String> getAllBrokers() {
        return executeParallelClientCalls(getBrokerECCallback, getNumberOfBrokerECPagesCallback,
                Brokers::getBrokerList, Broker::getBrokerCode,
                getBrokersPageLimit, null);
    }

    public void upsertCreditorInstitutionsAssociatedToBroker(String brokerCode) {
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        int numberOfPages = this.getCreditorInstitutionsAssociatedToBrokerPages.search(1, 0, brokerCode);

        log.debug("[Export-CI] delete old document");
        this.brokerInstitutionsRepository.findByBrokerCode(brokerCode).ifPresent(this.brokerInstitutionsRepository::delete);
        log.debug("[Export-CI] create new document for broker {}", brokerCode);
        this.brokerInstitutionsRepository.save(BrokerInstitutionsEntity.builder().brokerCode(brokerCode).build());

        // create parallel calls
        log.debug("[Export-CI] retrieve new data for the broker {} and updates its document", brokerCode);
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

    /**
     * ...
     *
     * @param paginatedSearch
     * @param pageNumberSearch
     * @param getResultList
     * @param mapInRequiredClass
     * @param limit
     * @param filterCode
     * @param <M>                the main type retrieved from main search, i.e. the type that contains the list of results and the PageInfo detail
     * @param <N>                the type of the nested object retreived from main search, i.e. the type related to the list of results
     * @param <R>                the type of the final result list generated by the 'mapInRequiredClass' callback
     * @return the set of object in type 'R', mapped by <code>mapInRequiredClass</code> callback.
     */
    public <M, N, R> Set<R> executeParallelClientCalls(
            PaginatedSearch<M> paginatedSearch, NumberOfTotalPagesSearch pageNumberSearch,
            GetResultList<M, N> getResultList, MapInRequiredClass<N, R> mapInRequiredClass,
            int limit, String filterCode
    ) {

        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        int numberOfPages = pageNumberSearch.search(1, 0, filterCode);

        List<CompletableFuture<Set<R>>> futures = new LinkedList<>();

        // create parallel calls
        CompletableFuture<Set<R>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> paginatedSearch.search(limit, page, filterCode))
                    .flatMap(response -> getResultList.get(response).stream())
                    .map(mapInRequiredClass::map)
                    .collect(Collectors.toSet());
        });
        futures.add(future);

        // join parallel calls
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(e -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
                .join();
    }


    private final PaginatedSearch<Brokers> getBrokerECCallback = (int limit, int page, String code) ->
            apiConfigClient.getBrokersEC(limit, page, code, null, null, null);

    private final PaginatedSearch<BrokerCreditorInstitutionDetails> getCreditorInstitutionsAssociatedToBroker = (int limit, int page, String code) ->
            apiConfigSCIntClient.getCreditorInstitutionsAssociatedToBroker(limit, page, true, code);

    private final NumberOfTotalPagesSearch getCreditorInstitutionsAssociatedToBrokerPages = (int limit, int page, String code) -> {
        var response = apiConfigSCIntClient.getCreditorInstitutionsAssociatedToBroker(limit, page, true, code);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / getBrokersPageLimit);
    };

    private final NumberOfTotalPagesSearch getNumberOfBrokerECPagesCallback = (int limit, int page, String code) -> {
        Brokers response = apiConfigClient.getBrokersEC(limit, page, null, null, null, null);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / getBrokersPageLimit);
    };

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
}
