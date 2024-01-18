package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;


import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.BrokerCreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbansList;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class AllPages {

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private ApiConfigSelfcareIntegrationClient apiConfigSCIntClient;

    @Autowired
    private ModelMapper modelMapper;


    @Value("${extraction.ibans.getBrokers.pageLimit}")
    private Integer getBrokersPageLimit;

    @Value("${extraction.ibans.getIbans.pageLimit}")
    private Integer getIbansPageLimit;

    @Value("${extraction.ibans.getCIByBroker.pageLimit}")
    private Integer getCIByBrokerPageLimit;


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
    public <M, N, R> Set<R> executeParallelClientCalls(PaginatedSearch<M> paginatedSearch, NumberOfTotalPagesSearch pageNumberSearch,
                                                       GetResultList<M, N> getResultList, MapInRequiredClass<N, R> mapInRequiredClass,
                                                       int limit, String filterCode) {

        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        int numberOfPages = pageNumberSearch.search(1, 0, filterCode);

        List<CompletableFuture<Set<R>>> futures = new LinkedList<>();

        // create parallel calls
        CompletableFuture<Set<R>> future = CompletableFuture.supplyAsync(() -> {
            if(mdcContextMap != null) {
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

    /**
     * @return the set of all brokers in pagoPA platform
     */
    public Set<String> getAllBrokers() {
        return executeParallelClientCalls(getBrokerECCallback, getNumberOfBrokerECPagesCallback,
                Brokers::getBrokerList, Broker::getBrokerCode,
                getBrokersPageLimit, null);
    }

    public Set<BrokerInstitutionEntity> getCreditorInstitutionsAssociatedToBroker(String brokerCode) {
        return executeParallelClientCalls(getCreditorInstitutionsAssociatedToBroker, getCreditorInstitutionsAssociatedToBrokerPages,
                BrokerCreditorInstitutionDetails::getCreditorInstitutions, convertCreditorInstitutionDetailToBrokerInstitutionEntity,
                getBrokersPageLimit, brokerCode);
    }


    private final PaginatedSearch<Brokers> getBrokerECCallback = (int limit, int page, String code) ->
            apiConfigClient.getBrokersEC(limit, page, code, null, null, null);

    private final PaginatedSearch<BrokerCreditorInstitutionDetails> getCreditorInstitutionsAssociatedToBroker = (int limit, int page, String code) ->
            apiConfigSCIntClient.getCreditorInstitutionsAssociatedToBroker(limit, page, true, code);

    private final NumberOfTotalPagesSearch getCreditorInstitutionsAssociatedToBrokerPages = (int limit, int page, String code) -> {
        var response = apiConfigSCIntClient.getCreditorInstitutionsAssociatedToBroker(limit, page, true, code);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / getBrokersPageLimit);
    };


    private final PaginatedSearch<IbansList> getIbansByBrokerCallback = (int limit, int page, String code) -> {
        List<String> codes = List.of(code.split(","));
        return apiConfigSCIntClient.getIbans(limit, page, codes);
    };

    private final PaginatedSearch<CreditorInstitutionsView> getCIsByBrokerCallback = (int limit, int page, String code) ->
            apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(limit, page, null, code, null, null, null, null, null);

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
        CreditorInstitutionsView response = apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(limit, page, null, code, null, null, null, null, null);
        return (int) Math.floor((double) response.getPageInfo().getTotalItems() / getCIByBrokerPageLimit);
    };


    private final MapInRequiredClass<CreditorInstitutionDetail, BrokerInstitutionEntity> convertCreditorInstitutionDetailToBrokerInstitutionEntity = (CreditorInstitutionDetail elem) ->
            modelMapper.map(elem, BrokerInstitutionEntity.class);
}