package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.ApiConfigCreditorInstitutionsOrderBy;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.AvailableCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CIPaymentContact;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionAndBrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionContactsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionInfoResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationEditResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.UpdateCreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResource;
import it.pagopa.selfcare.pagopa.backoffice.repository.TavoloOpRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.sanitizeLogParam;

@Slf4j
@Service
public class CreditorInstitutionService {

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final TavoloOpRepository operativeTableRepository;

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    private final ApiManagementService apiManagementService;

    @Autowired
    public CreditorInstitutionService(
            ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            TavoloOpRepository operativeTableRepository,
            ExternalApiClient externalApiClient, ModelMapper modelMapper,
            ApiManagementService apiManagementService
    ) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.operativeTableRepository = operativeTableRepository;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
        this.apiManagementService = apiManagementService;
    }

    /**
     * Retrieve a paginated list of creditor institutions from ApiConfig with the provided filters
     *
     * @param ciCode  creditor institution's tax code used to filter out results
     * @param name    creditor institution's name used to filter out results
     * @param enabled creditor institution's enabled flag used to filter out results
     * @param orderBy field of the creditor institution used to order results
     * @param sorting direction of the sort
     * @param limit   the size of the page
     * @param page    the number of the page
     * @return the paginated list of creditor institutions
     */
    public CreditorInstitutionsResource getCreditorInstitutions(
            String ciCode,
            String name,
            Boolean enabled,
            ApiConfigCreditorInstitutionsOrderBy orderBy,
            Sort.Direction sorting,
            Integer limit,
            Integer page
    ) {
        CreditorInstitutions dto =
                this.apiConfigClient.getCreditorInstitutions(ciCode, name, enabled, orderBy, sorting.name(), limit, page);
        return this.mapper.toResource(dto);
    }

    public CreditorInstitutionDetailsResource getCreditorInstitutionDetails(String ciCode) {
        CreditorInstitutionDetails dto = apiConfigClient.getCreditorInstitutionDetails(ciCode);
        return mapper.toResource(dto);
    }

    /**
     * Retrieve the creditor institution's segregation codes except those already used by the target creditor institution.
     *
     * @param ciTaxCode       creditor institution's tax code that own the station
     * @param targetCITaxCode tax code of the target creditor institution that will be associated to the station
     * @return the available segregation codes
     */
    public AvailableCodes getCreditorInstitutionSegregationCodes(String ciTaxCode, String targetCITaxCode) {
        return this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionSegregationCodes(ciTaxCode, targetCITaxCode);
    }

    /**
     * Associate the provided creditor institution to the given station.
     * <p>
     * Check if the provided tax code is a creditor institution tax code and if so, associates it to the given station and
     * updates the authorizer config for each broker's api keys by adding the specified segregation code
     *
     * @param ciTaxCode     creditor institution's tax code
     * @param institutionId broker's institution id
     * @param brokerTaxCode broker's tax code
     * @param dto           creditor institution - station association info
     * @return the creditor institution - station association info
     */
    public CreditorInstitutionStationEditResource associateStationToCreditorInstitution(
            String ciTaxCode,
            String institutionId,
            String brokerTaxCode,
            @NotNull CreditorInstitutionStationDto dto
    ) {
        checkIfIsCITaxCodeFailOtherwise(ciTaxCode);
        CreditorInstitutionStationEdit station = this.mapper.fromDto(dto);
        CreditorInstitutionStationEdit ecStation = this.apiConfigClient.createCreditorInstitutionStationRelationship(ciTaxCode, station);
        try {
            this.apiManagementService.updateBrokerAuthorizerConfiguration(institutionId, brokerTaxCode);
        } catch (Exception e) {
            log.error("Failed to update broker {} API key authorizations, revert associate station to CI operation",
                    sanitizeLogParam(brokerTaxCode), e);
            this.apiConfigClient.deleteCreditorInstitutionStationRelationship(ciTaxCode, ecStation.getStationCode());
            throw e;
        }
        return this.mapper.toResource(ecStation);
    }

    /**
     * Updates the association between creditor institution and station.
     * <p>
     * Check if the provided tax code is a creditor institution tax code and if so, updates the association to the given
     * station with the provided info
     *
     * @param ciTaxCode creditor institution's tax code
     * @param dto       creditor institution - station association info
     * @return the updated creditor institution - station association info
     */
    public CreditorInstitutionStationEditResource updateStationAssociationToCreditorInstitution(
            String ciTaxCode,
            @NotNull CreditorInstitutionStationDto dto
    ) {
        checkIfIsCITaxCodeFailOtherwise(ciTaxCode);
        CreditorInstitutionStationEdit station = this.mapper.fromDto(dto);
        CreditorInstitutionStationEdit ecStation = this.apiConfigClient.updateCreditorInstitutionStationRelationship(ciTaxCode, station.getStationCode(), station);
        return this.mapper.toResource(ecStation);
    }

    /**
     * Removes the association between creditor institution and station.
     * <p>
     * Removes the association and updates the authorizer config for each broker's api keys by removing
     * the segregation code of the association
     *
     * @param ciTaxCode     creditor institution's tax code
     * @param institutionId broker's institution id
     * @param brokerTaxCode broker's tax code
     */
    public void deleteCreditorInstitutionStationRelationship(
            String ciTaxCode,
            String stationCode,
            String institutionId,
            String brokerTaxCode
    ) {
        CreditorInstitutions ciForRollback =
                this.apiConfigClient.getCreditorInstitutionsByStation(stationCode, 1, 0, ciTaxCode);
        this.apiConfigClient.deleteCreditorInstitutionStationRelationship(ciTaxCode, stationCode);
        try {
            this.apiManagementService.updateBrokerAuthorizerConfiguration(institutionId, brokerTaxCode);
        } catch (Exception e) {
            log.error("Failed to update broker {} API key authorizations, revert dissociate station to CI operation",
                    sanitizeLogParam(brokerTaxCode), e);
            if (ciForRollback != null && ciForRollback.getCreditorInstitutionList() != null && ciForRollback.getCreditorInstitutionList().size() == 1) {
                CreditorInstitutionStationEdit dto =
                        this.modelMapper.map(ciForRollback.getCreditorInstitutionList().get(0), CreditorInstitutionStationEdit.class);
                dto.setStationCode(stationCode);
                dto.setAuxDigit(dto.getAuxDigit() == null ? 3L : dto.getAuxDigit());
                this.apiConfigClient.createCreditorInstitutionStationRelationship(ciTaxCode, dto);
            } else {
                log.error("Unable to rollback dissociate station ({}) to CI ({}) operation",
                        sanitizeLogParam(stationCode), sanitizeLogParam(ciTaxCode));
            }
            throw e;
        }
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

    public CreditorInstitutionDetailsResource updateCreditorInstitutionDetails(
            String ciCode,
            UpdateCreditorInstitutionDto dto
    ) {
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
            if (brokers != null && !ObjectUtils.isEmpty(brokers.getBrokerList())) {
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

        if (brokerResource == null && creditorInstitutionDetailsResource == null) {
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
     * @param ciTaxCode     creditor institution's tax code
     * @param institutionId creditor institution's identifier
     * @return the creditor institution's contacts
     */
    public CreditorInstitutionContactsResource getCreditorInstitutionContacts(String ciTaxCode, String institutionId) {
        Optional<TavoloOpEntity> optionalOperativeTable = this.operativeTableRepository.findByTaxCode(ciTaxCode);

        TavoloOpResource operativeTable = null;
        if (optionalOperativeTable.isPresent()) {
            operativeTable = this.modelMapper.map(optionalOperativeTable.get(), TavoloOpResource.class);
        }

        List<InstitutionProductUsers> institutionUserList =
                this.externalApiClient.getInstitutionProductUsers(
                        institutionId,
                        null,
                        null,
                        Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser())
                );

        return CreditorInstitutionContactsResource.builder()
                .operativeTable(operativeTable)
                .ciPaymentContacts(
                        institutionUserList.stream()
                                .map(user -> modelMapper.map(user, CIPaymentContact.class))
                                .toList()
                )
                .build();
    }

    /**
     * Retrieve the list of creditor institutions that can be associated to the specified station of the specified broker.
     * Add itself to the delegation list if the broker is of role {@link RoleType#CI} and filter out the creditor institutions
     * that are already associated to the station.
     *
     * @param stationCode station's code
     * @param brokerId    identifier of the broker that own the station
     * @return the list of creditor institution's
     */
    public CreditorInstitutionInfoResource getAvailableCreditorInstitutionsForStation(
            String stationCode,
            String brokerId,
            String ciName
    ) {
        List<CreditorInstitutionInfo> infoList = new ArrayList<>();
        List<String> delegations = getDelegationExternals(brokerId, ciName).parallelStream()
                .filter(delegation -> isCIDelegation(brokerId, delegation))
                .map(DelegationExternal::getTaxCode)
                .toList();

        int page = 0;
        int limit = 10;
        int fromIndex = 0;
        while (infoList.size() < 10 && fromIndex < delegations.size()) {
            int toIndex = Math.min(fromIndex + limit, delegations.size());
            List<String> delegationExternalTaxCodes = delegations.subList(fromIndex, toIndex);

            if (!delegationExternalTaxCodes.isEmpty()) {
                infoList.addAll(getAvailableCIInfo(stationCode, delegationExternalTaxCodes));
            }
            page++;
            fromIndex = page * limit;
        }

        // filter by roles
        return CreditorInstitutionInfoResource.builder()
                .creditorInstitutionInfos(infoList)
                .build();
    }

    private List<CreditorInstitutionInfo> getAvailableCIInfo(
            String stationCode,
            List<String> delegationExternalTaxCodes
    ) {
        return this.apiConfigSelfcareIntegrationClient
                .getStationCreditorInstitutions(stationCode, delegationExternalTaxCodes).parallelStream()
                .map(ciInfo -> this.modelMapper.map(ciInfo, CreditorInstitutionInfo.class))
                .toList();
    }

    private List<DelegationExternal> getDelegationExternals(String brokerId, String ciName) {
        List<DelegationExternal> response = this.externalApiClient
                .getBrokerDelegation(
                        null,
                        brokerId,
                        "prod-pagopa",
                        "FULL",
                        ciName
                );

        List<DelegationExternal> delegationExternals = new ArrayList<>(response);
        InstitutionResponse broker = modelMapper.map(this.externalApiClient.getInstitution(brokerId), InstitutionResponse.class);
        if (brokerCanBeAddedToDelegation(delegationExternals, broker, ciName)) {
            delegationExternals.add(DelegationExternal.builder()
                    .taxCode(broker.getTaxCode())
                    .institutionName(broker.getDescription())
                    .institutionType(broker.getInstitutionType().toString())
                    .brokerId(brokerId)
                    .build()
            );
        }
        return delegationExternals;
    }

    private boolean brokerCanBeAddedToDelegation(
            List<DelegationExternal> delegationExternals,
            InstitutionResponse broker,
            String ciNameFilter
    ) {
        return (
                RoleType.CI.equals(RoleType.fromSelfcareRole(broker.getTaxCode(), broker.getInstitutionType().name()))
                        || RoleType.PT.equals(RoleType.fromSelfcareRole(broker.getTaxCode(), broker.getInstitutionType().name()))
        )
                && (StringUtils.isBlank(ciNameFilter) || broker.getDescription().toLowerCase().contains(ciNameFilter.toLowerCase()))
                && delegationExternals.parallelStream().noneMatch(delegationExternal -> delegationExternal.getTaxCode().equals(broker.getTaxCode()));
    }

    private void checkIfIsCITaxCodeFailOtherwise(String ciTaxCode) {
        try {
            this.apiConfigClient.getCreditorInstitutionDetails(ciTaxCode);
        } catch (FeignException e) {
            throw new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, ciTaxCode);
        }
    }

    private boolean isCIDelegation(String brokerId, DelegationExternal delegation) {
        return delegation != null
                && (
                RoleType.CI.equals(RoleType.fromSelfcareRole(delegation.getTaxCode(), delegation.getInstitutionType()))
                        || (
                        delegation.getBrokerId().equals(brokerId)
                                && RoleType.PT.equals(RoleType.fromSelfcareRole(delegation.getTaxCode(), delegation.getInstitutionType()))
                )
        );
    }
}
