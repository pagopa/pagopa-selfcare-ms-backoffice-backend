package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.BrokerEcDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.MyCIResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrokerService {

    private final CreditorInstitutionMapper mapper =
            Mappers.getMapper(CreditorInstitutionMapper.class);

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final ModelMapper modelMapper;

    private final ExternalApiClient externalApiClient;

    @Autowired
    public BrokerService(ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            ModelMapper modelMapper, ExternalApiClient externalApiClient) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.modelMapper = modelMapper;
        this.externalApiClient = externalApiClient;
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

    public StationDetailsResourceList getStationsDetailsListByBroker(String brokerId,
            String stationId, Integer limit, Integer page) {
        StationDetailsList response = apiConfigSelfcareIntegrationClient
                .getStationsDetailsListByBroker(brokerId, stationId, limit, page);
        return modelMapper.map(response, StationDetailsResourceList.class);
    }


    public BrokersResource getBrokersEC(Integer limit, Integer page, String code, String name,
            String orderby, String ordering) {
        Brokers response = apiConfigClient.getBrokersEC(limit, page, code, name, orderby, ordering);
        return modelMapper.map(response, BrokersResource.class);
    }


    /**
     * Retrieves the list of broker's creditor institution delegation for the specified broker enriched with:
     * <ul>
     * <li>the institution's station count
     * <li>the institution's CBILL code
     * 
     * @param brokerId the broker identifier
     * @param brokerCode the broker tax code
     * @return the enriched list of broker's delegations
     */
    public List<MyCIResource> getBrokerDelegation(String brokerCode, String brokerId) {
        List<DelegationExternal> delegationResponse =
                this.externalApiClient.getBrokerDelegation(null, brokerId, "prod-pagopa", "FULL");

        List<MyCIResource> delegationList = delegationResponse.stream()
                .map(elem -> modelMapper.map(elem, MyCIResource.class)).toList();

        // filter by roles
        delegationList = delegationList.parallelStream().filter(Objects::nonNull)
                .filter(delegation -> RoleType.EC
                        .equals(RoleType.fromSelfcareRole(delegation.getInstitutionType())))
                .toList();

        delegationList.forEach(delegation -> {
            delegation.setInstitutionStationCount(
                    getInstitutionsStationCount(brokerCode));
            delegation.setCbillCode(getInstitutionCBILLCode(delegation.getInstitutionTaxCode()));
        });

        return delegationList;
    }

    private Long getInstitutionsStationCount(String brokerId) {
        // TODO filter stations by creditor institution tax code
        StationDetailsList response = this.apiConfigSelfcareIntegrationClient
                .getStationsDetailsListByBroker(brokerId, null, 10, 0);
        if (response.getPageInfo() != null && response.getPageInfo().getTotalItems() != null) {
            return response.getPageInfo().getTotalItems();
        }
        return 0L;
    }

    private String getInstitutionCBILLCode(String institutionTaxCode) {
        CreditorInstitutionDetails dto =
                this.apiConfigClient.getCreditorInstitutionDetails(institutionTaxCode);
        if (dto != null) {
            return dto.getCbillCode();
        }
        return null;
    }
}
