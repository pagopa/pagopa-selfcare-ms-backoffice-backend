package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResource;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CreditorInstitutionService {

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final OperativeTableService operativeTableService;

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    @Autowired
    public CreditorInstitutionService(
            ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            OperativeTableService operativeTableService,
            ExternalApiClient externalApiClient, ModelMapper modelMapper) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.operativeTableService = operativeTableService;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
    }

    public CreditorInstitutionsResource getCreditorInstitutions(Integer limit, Integer page, String ciCode, String name, String sorting) {
        CreditorInstitutions dto = apiConfigClient.getCreditorInstitutions(limit, page, ciCode, name, sorting);
        return mapper.toResource(dto);
    }

    public CreditorInstitutionDetailsResource getCreditorInstitutionDetails(String ciCode) {
        CreditorInstitutionDetails dto = apiConfigClient.getCreditorInstitutionDetails(ciCode);
        return mapper.toResource(dto);
    }

    public CreditorInstitutionAssociatedCodeList getCreditorInstitutionSegregationcodes(String ciCode) {
        return apiConfigSelfcareIntegrationClient.getCreditorInstitutionSegregationcodes(ciCode);
    }

    public CreditorInstitutionStationEditResource associateStationToCreditorInstitution(String ecCode, @NotNull CreditorInstitutionStationDto dto) {
        try {
            apiConfigClient.getCreditorInstitutionDetails(ecCode);
        } catch (FeignException e) {
            throw new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, ecCode);
        }
        CreditorInstitutionStationEdit station = mapper.fromDto(dto);
        CreditorInstitutionStationEdit ecStation = apiConfigClient.createCreditorInstitutionStationRelationship(ecCode, station);
        return mapper.toResource(ecStation);
    }

    public void deleteCreditorInstitutionStationRelationship(String ecCode, String stationCode) {
        apiConfigClient.deleteCreditorInstitutionStationRelationship(ecCode, stationCode);
    }


    public CreditorInstitutionDetailsResource createCreditorInstitution(CreditorInstitutionDto creditorInstitutionDto) {
        CreditorInstitutionDetails dto = mapper.fromDto(creditorInstitutionDto);
        dto = apiConfigClient.createCreditorInstitution(dto);
        return mapper.toResource(dto);
    }

    public CreditorInstitutionDetailsResource createCIAndBroker(CreditorInstitutionAndBrokerDto dto) {
        CreditorInstitutionDetails creditorInstitutionDetails = mapper.fromDto(dto.getCreditorInstitutionDto());
        creditorInstitutionDetails = apiConfigClient.createCreditorInstitution(creditorInstitutionDetails);
        apiConfigClient.createBroker(BrokerMapper.fromDto(dto.getBrokerDto()));
        return mapper.toResource(creditorInstitutionDetails);
    }

    public CreditorInstitutionDetailsResource updateCreditorInstitutionDetails(String ciCode, UpdateCreditorInstitutionDto dto) {
        CreditorInstitutionDetails creditorInstitutionDetails = mapper.fromDto(dto);
        creditorInstitutionDetails = apiConfigClient.updateCreditorInstitutionDetails(ciCode, creditorInstitutionDetails);
        return mapper.toResource(creditorInstitutionDetails);
    }


    public BrokerAndEcDetailsResource getBrokerAndEcDetails(String brokerEcCode) {

        BrokerResource brokerResource = null;
        CreditorInstitutionDetailsResource creditorInstitutionDetailsResource = null;
        Brokers brokers;
        CreditorInstitutionDetails creditorInstitutionDetails;

        try {
            brokers = apiConfigClient.getBrokersEC(1, 0, brokerEcCode, null, null, "ASC");
            if(brokers != null && !ObjectUtils.isEmpty(brokers.getBrokerList())) {
                brokerResource = BrokerMapper.toResource(brokers.getBrokerList().get(0));
            }
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerOrEcDetails - Not BrokerEC found");
        }

        try {
            creditorInstitutionDetails = apiConfigClient.getCreditorInstitutionDetails(brokerEcCode);
            creditorInstitutionDetailsResource = mapper.toResource(creditorInstitutionDetails);
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerOrEcDetails - Not CreditorInstitution found");
        }

        if(brokerResource == null && creditorInstitutionDetailsResource == null) {
            throw new AppException(AppError.ACTOR_NOT_FOUND, brokerEcCode);
        }
        BrokerAndEcDetailsResource resource = new BrokerAndEcDetailsResource();
        resource.setBrokerDetailsResource(brokerResource);
        resource.setCreditorInstitutionDetailsResource(creditorInstitutionDetailsResource);

        return resource;
    }

    /**
     * Retrieve the operative table and the payment contacts list of the creditor institution with the provided
     * tax code and institution's id
     *
     * @param ciTaxCode creditor institution's tax code
     * @param institutionId creditor institution's identifier
     * @return the creditor institution's contacts
     */
    public CreditorInstitutionContactsResource getCreditorInstitutionContacts(String ciTaxCode, String institutionId) {
        TavoloOpResource operativeTable = null;
        try {
            operativeTable = this.operativeTableService.getOperativeTable(ciTaxCode);
        } catch (AppException e) {
            log.warn("Operative table for creditor institution with tax code {} not found", ciTaxCode, e);
        }

        // TODO: check if can be enabled or must be deleted
        /*List<InstitutionProductUsers> institutionUserList =
                this.externalApiClient.getInstitutionProductUsers(
                        institutionId,
                        null,
                        null,
                        Collections.singletonList("admin")
                );*/

        return CreditorInstitutionContactsResource.builder()
                .operativeTable(operativeTable)
                /*.ciPaymentContacts(
                        institutionUserList.stream()
                                .map(user -> modelMapper.map(user, CIPaymentContact.class))
                                .toList()
                )*/
                .build();
    }
}
