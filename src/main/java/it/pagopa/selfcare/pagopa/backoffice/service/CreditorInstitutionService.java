package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
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
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class CreditorInstitutionService {

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

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
}
