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
    public BrokerService(
            ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            ModelMapper modelMapper,
            ExternalApiClient externalApiClient
    ) {
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
     * @return the enriched list of broker's delegations
     */
    public List<MyCIResource> getCIBrokerDelegation(
            String brokerCode,
            String brokerId,
            String ciName,
            Integer page,
            Integer limit
    ) {
        List<DelegationExternal> delegationResponse =
                this.externalApiClient.getBrokerDelegation(null, brokerId, "prod-pagopa", "FULL");

        List<MyCIResource> delegationList = delegationResponse.stream()
                .map(elem -> modelMapper.map(elem, MyCIResource.class)).toList();

        // filter by roles
        delegationList = delegationList.parallelStream()
                .filter(Objects::nonNull)
                .filter(delegation -> RoleType.EC
                        .equals(RoleType.fromSelfcareRole(delegation.getInstitutionType())))
                .filter(delegation -> institutionNameMatchFilter(ciName, delegation))
                .skip(page != 0 ? (long) page * limit : 0)
                .limit(limit)
                .toList();

        delegationList.parallelStream().forEach(delegation -> {
            String institutionTaxCode = delegation.getInstitutionTaxCode();
            delegation.setInstitutionStationCount(getInstitutionsStationCount(brokerCode, institutionTaxCode));
            delegation.setCbillCode(getInstitutionCBILLCode(institutionTaxCode));
        });

        return delegationList;
    }

    private boolean institutionNameMatchFilter(String ciName, MyCIResource delegation) {
        if (ciName == null || ciName.trim().isEmpty()) {
            return true;
        }
        if (delegation.getInstitutionName() == null) {
            return false;
        }
        return delegation.getInstitutionName().toLowerCase().contains(ciName.toLowerCase());
    }

    private Long getInstitutionsStationCount(String brokerCode, String institutionTaxCode) {
        StationDetailsList response = this.apiConfigSelfcareIntegrationClient
                .getStationsDetailsListByBroker(brokerCode, null, institutionTaxCode, 1, 0);
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
