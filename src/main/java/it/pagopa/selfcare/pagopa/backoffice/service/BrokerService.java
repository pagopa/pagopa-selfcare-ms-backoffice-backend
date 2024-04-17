package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.BrokerEcDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getWrapperEntityOperationsSortedList;

@Slf4j
@Service
public class BrokerService {

    private final CreditorInstitutionMapper mapper =
            Mappers.getMapper(CreditorInstitutionMapper.class);

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final ModelMapper modelMapper;

    private final ExternalApiClient externalApiClient;

    private final WrapperService wrapperService;

    @Autowired
    public BrokerService(
            ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            ModelMapper modelMapper,
            ExternalApiClient externalApiClient, WrapperService wrapperService) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.modelMapper = modelMapper;
        this.externalApiClient = externalApiClient;
        this.wrapperService = wrapperService;
    }

    public BrokerResource createBroker(BrokerDto brokerDto) {
        BrokerDetails broker = apiConfigClient.createBroker(BrokerMapper.fromDto(brokerDto));
        return BrokerMapper.toResource(broker);
    }


    public BrokerDetailsResource updateBrokerForCI(BrokerEcDto brokerEcDto, String brokerCode) {
        BrokerDetails dto = mapper.fromDto(brokerEcDto);
        dto = apiConfigClient.updateBrokerEc(dto, brokerCode);
        return mapper.toResource(dto);
    }

    public StationDetailsResourceList getStationsDetailsListByBroker(
            String brokerCode,
            String stationId,
            Integer limit,
            Integer page
    ) {
        StationDetailsList response = apiConfigSelfcareIntegrationClient
                .getStationsDetailsListByBroker(brokerCode, stationId, null, limit, page);
        return modelMapper.map(response, StationDetailsResourceList.class);
    }


    public BrokersResource getBrokersEC(Integer limit, Integer page, String code, String name,
                                        String orderby, String ordering) {
        Brokers response = apiConfigClient.getBrokersEC(limit, page, code, name, orderby, ordering);
        return modelMapper.map(response, BrokersResource.class);
    }


    /**
     * Retrieves the paginated list of broker's creditor institution delegation for the specified broker
     * enriched with:
     * <ul>
     * <li>the institution's station count
     * <li>the institution's CBILL code
     *
     * @param brokerCode the broker tax code
     * @param brokerId   the broker identifier
     * @param ciName     creditor institution's name, used for filtering result
     * @param page       page number
     * @param limit      number of element in the page
     * @return the enriched page of broker's delegations
     */
    public CIBrokerDelegationPage getCIBrokerDelegation(
            String brokerCode,
            String brokerId,
            String ciName,
            Integer page,
            Integer limit
    ) {
        List<DelegationExternal> delegationResponse = getDelegationResponse(brokerId, ciName);

        int fromIndex = page * limit;
        List<CIBrokerDelegationResource> selectedDelegationPage;
        if (fromIndex > delegationResponse.size()) {
            // if the index is out of bound 
            selectedDelegationPage = Collections.emptyList();
        } else {
            // we simulate the pagination of the response, so we split the response
            List<DelegationExternal> subList = delegationResponse.subList(fromIndex, Math.min(fromIndex + limit, delegationResponse.size()));

            selectedDelegationPage = subList.parallelStream()
                    .map(elem -> enrichDelegationResponse(brokerCode, elem))
                    .toList();
        }

        return buildDelegationPageResponse(selectedDelegationPage, limit, page, delegationResponse.size());
    }


    /**
     * Retrieve the paginated association info between broker's stations and creditor institutions for
     * the specified broker's tax code and creditor institution's tax code.
     *
     * @param brokerTaxCode broker's tax code
     * @param ciTaxCode     creditor institution's tax code
     * @param stationCode   station identifier
     * @param page          page number
     * @param limit         page size
     * @return the association info
     */
    public CIBrokerStationPage getCIBrokerStations(
            String brokerTaxCode,
            String ciTaxCode,
            String stationCode,
            Integer page,
            Integer limit
    ) {
        CreditorInstitutionsView creditorInstitutionsView = this.apiConfigClient.getCreditorInstitutionsAssociatedToBrokerStations(
                limit,
                page,
                ciTaxCode,
                brokerTaxCode,
                stationCode,
                null,
                null,
                null,
                null,
                null
        );

        List<CIBrokerStationResource> ciBrokerStations = creditorInstitutionsView.getCreditorInstitutionList().stream()
                .map(ciView -> this.modelMapper.map(ciView, CIBrokerStationResource.class))
                .map(this::enrichBrokerStation)
                .toList();

        return CIBrokerStationPage.builder()
                .ciBrokerStations(ciBrokerStations)
                .pageInfo(creditorInstitutionsView.getPageInfo())
                .build();
    }

    /**
     * Deletes the Creditor Institution's broker
     *
     * @param brokerTaxCode Tax code of the broker to delete
     */
    public void deleteCIBroker(String brokerTaxCode) {
        this.apiConfigClient.deleteCIBroker(brokerTaxCode);
    }

    private CIBrokerDelegationPage buildDelegationPageResponse(
            List<CIBrokerDelegationResource> delegationPage,
            Integer limit,
            Integer page,
            int completeDelegationListSize
    ) {
        return CIBrokerDelegationPage.builder()
                .ciBrokerDelegationResources(delegationPage)
                .pageInfo(PageInfo.builder()
                        .itemsFound(delegationPage.size())
                        .limit(limit)
                        .page(page)
                        .totalPages((int) Math.ceil(completeDelegationListSize * 1.0 / limit))
                        .totalItems((long) completeDelegationListSize)
                        .build())
                .build();
    }

    private boolean institutionNameMatchFilter(String filterCIName, String delegationInstitutionName) {
        if (filterCIName == null || filterCIName.trim().isEmpty()) {
            return true;
        }
        if (delegationInstitutionName == null) {
            return false;
        }
        return delegationInstitutionName.toLowerCase().contains(filterCIName.toLowerCase());
    }

    private Long getInstitutionsStationCount(String brokerCode, String institutionTaxCode) {
        StationDetailsList response = this.apiConfigSelfcareIntegrationClient
                .getStationsDetailsListByBroker(brokerCode, null, institutionTaxCode, 1, 0);
        if (response.getPageInfo().getTotalItems() != null) {
            return response.getPageInfo().getTotalItems();
        }
        return 0L;
    }

    private String getInstitutionCBILLCode(String institutionTaxCode) {
        CreditorInstitutionDetails dto = this.apiConfigClient.getCreditorInstitutionDetails(institutionTaxCode);
        if (dto != null) {
            return dto.getCbillCode();
        }
        return null;
    }

    private CIBrokerStationResource enrichBrokerStation(CIBrokerStationResource ciBrokerStation) {
        Optional<WrapperEntities> optionalResult = this.wrapperService.findByIdOptional(ciBrokerStation.getStationCode());
        if (optionalResult.isEmpty()) {
            log.warn("Station with id {} not found in wrapper store", ciBrokerStation.getStationCode());
            return ciBrokerStation;
        }
        WrapperEntities result = optionalResult.get();
        StationDetails details = (StationDetails) getWrapperEntityOperationsSortedList(result).get(0).getEntity();

        ciBrokerStation.setActivationDate(details.getActivationDate());
        ciBrokerStation.setModifiedAt(result.getModifiedAt());
        return ciBrokerStation;
    }

    /**
     * Add some data (number of the stations and CBILL code) from ApiConfig
     *
     * @param brokerCode         the broker code
     * @param delegationExternal delegation info from selfcare
     * @return a new Delegation with the number of the stations and CBILL code
     */
    private CIBrokerDelegationResource enrichDelegationResponse(String brokerCode, DelegationExternal delegationExternal) {
        var delegation = modelMapper.map(delegationExternal, CIBrokerDelegationResource.class);
        String institutionTaxCode = delegation.getInstitutionTaxCode();
        delegation.setInstitutionStationCount(getInstitutionsStationCount(brokerCode, institutionTaxCode));
        try {
            delegation.setCbillCode(getInstitutionCBILLCode(institutionTaxCode));
            delegation.setIsInstitutionSignedIn(true);
        } catch (FeignException.NotFound e) {
            delegation.setIsInstitutionSignedIn(false);
        }
        return delegation;
    }

    /**
     * It retrieves the Creditor Institution with delegate for the broker from selfcare
     *
     * @param brokerId filtered by broker
     * @param ciName   filtered by name
     * @return a filtered list of delegations
     */
    private List<DelegationExternal> getDelegationResponse(String brokerId, String ciName) {
        List<DelegationExternal> delegationResponse = this.externalApiClient.getBrokerDelegation(null, brokerId, "prod-pagopa", "FULL");

        // filter by roles
        return delegationResponse.parallelStream()
                .filter(Objects::nonNull)
                .filter(delegation -> RoleType.EC.equals(RoleType.fromSelfcareRole(delegation.getInstitutionType())))
                .filter(delegation -> institutionNameMatchFilter(ciName, delegation.getInstitutionName()))
                .toList();
    }
}
