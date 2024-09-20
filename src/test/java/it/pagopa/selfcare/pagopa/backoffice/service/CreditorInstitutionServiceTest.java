package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.entity.TavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.ApiConfigCreditorInstitutionsOrderBy;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.AvailableCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CIPaymentContact;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionAndBrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionContactsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionInfoResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationEditResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.UpdateCreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.repository.TavoloOpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CreditorInstitutionService.class, MappingsConfiguration.class})
class CreditorInstitutionServiceTest {

    private static final String BROKER_ID = "brokerId";
    private static final String STATION_CODE = "stationCode";
    private static final String CI_TAX_CODE_1 = "12345677";
    private static final String CI_TAX_CODE_2 = "12345666";
    private static final String INSTITUTION_ID = "institutionId";
    private static final String BROKER_TAX_CODE = "brokerTaxCode";
    private static final String CI_TAX_CODE = "12345678900";
    private static final String STATION_CODE1 = "00000000000_01";

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @MockBean
    private TavoloOpRepository operativeTableRepository;

    @MockBean
    private ExternalApiClient externalApiClient;

    @MockBean
    private ApiManagementService apiManagementService;

    @Autowired
    private CreditorInstitutionService service;

    @Test
    void getCreditorInstitutions_ok() throws IOException {
        when(apiConfigClient.getCreditorInstitutions(anyString(), anyString(), anyBoolean(), any(), any(), anyInt(), anyInt()))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/get_creditor_institutions_ok.json", CreditorInstitutions.class));

        CreditorInstitutionsResource result =
                assertDoesNotThrow(() ->
                        service.getCreditorInstitutions(
                                "12345",
                                "comune",
                                true,
                                ApiConfigCreditorInstitutionsOrderBy.NAME,
                                Sort.Direction.ASC,
                                50,
                                0
                        ));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionList());
        assertFalse(result.getCreditorInstitutionList().isEmpty());
    }

    @Test
    void getCreditorInstitutions_ok_without_filters() throws IOException {
        when(apiConfigClient.getCreditorInstitutions(
                eq(null),
                eq(null),
                eq(null),
                eq(ApiConfigCreditorInstitutionsOrderBy.NAME),
                eq(Sort.Direction.ASC.name()),
                anyInt(),
                anyInt()
        )).thenReturn(TestUtil.fileToObject("response/apiconfig/get_creditor_institutions_ok.json", CreditorInstitutions.class));

        CreditorInstitutionsResource result =
                assertDoesNotThrow(() ->
                        service.getCreditorInstitutions(
                                null,
                                null,
                                null,
                                ApiConfigCreditorInstitutionsOrderBy.NAME,
                                Sort.Direction.ASC,
                                50,
                                0
                        ));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionList());
        assertFalse(result.getCreditorInstitutionList().isEmpty());
    }

    @Test
    void getCreditorInstitutions_ko() {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.getCreditorInstitutions(anyString(), anyString(), anyBoolean(), any(), any(), anyInt(), anyInt()))
                .thenThrow(feignException);

        FeignException e = assertThrows(FeignException.class,
                () -> service.getCreditorInstitutions(
                        "12345",
                        "comune",
                        true,
                        ApiConfigCreditorInstitutionsOrderBy.NAME,
                        Sort.Direction.ASC,
                        50,
                        0
                ));

        assertNotNull(e);
    }

    @Test
    void getCreditorInstitutionDetails_ok() throws IOException {
        when(apiConfigClient.getCreditorInstitutionDetails(anyString()))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/get_creditor_institution_details_ok.json", CreditorInstitutionDetails.class));

        CreditorInstitutionDetailsResource result = service.getCreditorInstitutionDetails(CI_TAX_CODE);

        assertNotNull(result);
    }

    @Test
    void getCreditorInstitutionDetails_ko() {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenThrow(feignException);

        assertThrows(FeignException.class, () -> service.getCreditorInstitutionDetails(CI_TAX_CODE));
    }

    @Test
    void getCreditorInstitutionSegregationCodes_ok() {
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionSegregationCodes(anyString(), anyString()))
                .thenReturn(AvailableCodes.builder().availableCodeList(Collections.singletonList("2")).build());

        AvailableCodes result = assertDoesNotThrow(() -> service.getCreditorInstitutionSegregationCodes(CI_TAX_CODE, "111111"));

        assertNotNull(result);
    }

    @Test
    void getCreditorInstitutionSegregationCodes_ko() {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionSegregationCodes(anyString(), anyString())).thenThrow(feignException);

        assertThrows(FeignException.class, () -> service.getCreditorInstitutionSegregationCodes(CI_TAX_CODE, "111111"));
    }

    @Test
    void associateStationToCreditorInstitution_ok() throws IOException {
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenReturn(new CreditorInstitutionDetails());
        when(apiConfigClient.createCreditorInstitutionStationRelationship(anyString(), any(CreditorInstitutionStationEdit.class)))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/post_creditor_institution_station_association_ok.json", CreditorInstitutionStationEdit.class));

        CreditorInstitutionStationDto dto = TestUtil.fileToObject("request/post_creditor_institution_station_association.json", CreditorInstitutionStationDto.class);
        CreditorInstitutionStationEditResource result = service.associateStationToCreditorInstitution(CI_TAX_CODE, INSTITUTION_ID, BROKER_TAX_CODE, dto);

        assertNotNull(result);

        verify(apiManagementService).updateBrokerAuthorizerSegregationCodesMetadata(INSTITUTION_ID, BROKER_TAX_CODE);
    }

    @Test
    void associateStationToCreditorInstitution_ko() throws IOException {
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenThrow(FeignException.InternalServerError.class);

        CreditorInstitutionStationDto dto = TestUtil.fileToObject("request/post_creditor_institution_station_association.json", CreditorInstitutionStationDto.class);
        assertThrows(AppException.class, () -> service.associateStationToCreditorInstitution(CI_TAX_CODE, INSTITUTION_ID, BROKER_TAX_CODE, dto));

        verify(apiConfigClient, never()).createCreditorInstitutionStationRelationship(anyString(), any(CreditorInstitutionStationEdit.class));
        verify(apiManagementService, never()).updateBrokerAuthorizerSegregationCodesMetadata(INSTITUTION_ID, BROKER_TAX_CODE);
    }

    @Test
    void associateStationToCreditorInstitutionFailOnAuthorizerUpdateExpectRollback() throws IOException {
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenReturn(new CreditorInstitutionDetails());
        when(apiConfigClient.createCreditorInstitutionStationRelationship(anyString(), any(CreditorInstitutionStationEdit.class)))
                .thenReturn(
                        TestUtil.fileToObject(
                                "response/apiconfig/post_creditor_institution_station_association_ok.json",
                                CreditorInstitutionStationEdit.class)
                );
        doThrow(AppException.class).when(apiManagementService).updateBrokerAuthorizerSegregationCodesMetadata(anyString(), anyString());

        CreditorInstitutionStationDto dto =
                TestUtil.fileToObject("request/post_creditor_institution_station_association.json", CreditorInstitutionStationDto.class);
        assertThrows(AppException.class, () -> service.associateStationToCreditorInstitution(CI_TAX_CODE, INSTITUTION_ID, BROKER_TAX_CODE, dto));

        verify(apiManagementService).updateBrokerAuthorizerSegregationCodesMetadata(INSTITUTION_ID, BROKER_TAX_CODE);
        verify(apiConfigClient).deleteCreditorInstitutionStationRelationship(anyString(), anyString());
    }

    @Test
    void updateStationAssociationToCreditorInstitution_ok() throws IOException {
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenReturn(new CreditorInstitutionDetails());
        when(apiConfigClient.updateCreditorInstitutionStationRelationship(anyString(),anyString(), any(CreditorInstitutionStationEdit.class)))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/post_creditor_institution_station_association_ok.json", CreditorInstitutionStationEdit.class));

        CreditorInstitutionStationDto dto = TestUtil.fileToObject("request/post_creditor_institution_station_association.json", CreditorInstitutionStationDto.class);
        CreditorInstitutionStationEditResource result = service.updateStationAssociationToCreditorInstitution(CI_TAX_CODE, dto);

        assertNotNull(result);
    }

    @Test
    void updateStationAssociationToCreditorInstitution_ko() throws IOException {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenThrow(feignException);

        CreditorInstitutionStationDto dto = TestUtil.fileToObject("request/post_creditor_institution_station_association.json", CreditorInstitutionStationDto.class);
        assertThrows(AppException.class, () -> service.updateStationAssociationToCreditorInstitution(CI_TAX_CODE, dto));
        verify(apiConfigClient, times(0)).updateCreditorInstitutionStationRelationship(anyString(), anyString(),any(CreditorInstitutionStationEdit.class));
    }

    @Test
    void deleteCreditorInstitutionStationRelationship_ok() {
        assertDoesNotThrow(() -> service.deleteCreditorInstitutionStationRelationship(CI_TAX_CODE, STATION_CODE1, INSTITUTION_ID, BROKER_TAX_CODE));

        verify(apiConfigClient).deleteCreditorInstitutionStationRelationship(anyString(), anyString());
        verify(apiManagementService).updateBrokerAuthorizerSegregationCodesMetadata(INSTITUTION_ID, BROKER_TAX_CODE);
    }

    @Test
    void deleteCreditorInstitutionStationRelationship_ko() {
        doThrow(FeignException.InternalServerError.class).when(apiConfigClient)
                .deleteCreditorInstitutionStationRelationship(anyString(), anyString());

        assertThrows(FeignException.class, () ->
                service.deleteCreditorInstitutionStationRelationship(CI_TAX_CODE, STATION_CODE1, INSTITUTION_ID, BROKER_TAX_CODE));
    }

    @Test
    void deleteCreditorInstitutionStationRelationshipFailOnAuthorizerUpdateExpectRollback() {
        doThrow(AppException.class).when(apiManagementService).updateBrokerAuthorizerSegregationCodesMetadata(anyString(), anyString());
        when(apiConfigClient.getCreditorInstitutionsByStation(STATION_CODE1, 1, 0, CI_TAX_CODE))
                .thenReturn(buildCreditorInstitutions());
        assertThrows(AppException.class, () ->
                service.deleteCreditorInstitutionStationRelationship(CI_TAX_CODE, STATION_CODE1, INSTITUTION_ID, BROKER_TAX_CODE));

        verify(apiConfigClient).deleteCreditorInstitutionStationRelationship(anyString(), anyString());
        verify(apiConfigClient).createCreditorInstitutionStationRelationship(anyString(), any());
    }

    @Test
    void createCreditorInstitution_ok() throws IOException {
        when(apiConfigClient.createCreditorInstitution(any(CreditorInstitutionDetails.class)))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/post_creditor_institution_ok.json", CreditorInstitutionDetails.class));

        CreditorInstitutionDto dto = TestUtil.fileToObject("request/post_creditor_institution.json", CreditorInstitutionDto.class);
        CreditorInstitutionDetailsResource result = service.createCreditorInstitution(dto);

        assertNotNull(result);
    }

    @Test
    void createCreditorInstitution_ko() throws IOException {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.createCreditorInstitution(any(CreditorInstitutionDetails.class))).thenThrow(feignException);

        CreditorInstitutionDto dto = TestUtil.fileToObject("request/post_creditor_institution.json", CreditorInstitutionDto.class);
        assertThrows(FeignException.class, () -> service.createCreditorInstitution(dto));
    }

    @Test
    void createCIAndBroker_ok() throws IOException {
        when(apiConfigClient.createCreditorInstitution(any(CreditorInstitutionDetails.class)))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/post_creditor_institution_ok.json", CreditorInstitutionDetails.class));
        when(apiConfigClient.createBroker(any(BrokerDetails.class)))
                .thenReturn(new BrokerDetails());

        CreditorInstitutionAndBrokerDto dto = TestUtil.fileToObject("request/post_creditor_institution_and_broker.json", CreditorInstitutionAndBrokerDto.class);
        CreditorInstitutionDetailsResource result = service.createCIAndBroker(dto);

        assertNotNull(result);
    }

    @Test
    void createCIAndBroker_ko() throws IOException {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.createCreditorInstitution(any(CreditorInstitutionDetails.class))).thenThrow(feignException);

        CreditorInstitutionAndBrokerDto dto = TestUtil.fileToObject("request/post_creditor_institution_and_broker.json", CreditorInstitutionAndBrokerDto.class);
        assertThrows(FeignException.class, () -> service.createCIAndBroker(dto));
        verify(apiConfigClient, times(0)).createBroker(any(BrokerDetails.class));
    }

    @Test
    void updateCreditorInstitution_ok() throws IOException {
        when(apiConfigClient.updateCreditorInstitutionDetails(anyString(), any(CreditorInstitutionDetails.class)))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/post_creditor_institution_ok.json", CreditorInstitutionDetails.class));

        UpdateCreditorInstitutionDto dto = TestUtil.fileToObject("request/post_creditor_institution.json", UpdateCreditorInstitutionDto.class);
        CreditorInstitutionDetailsResource result = service.updateCreditorInstitutionDetails(CI_TAX_CODE, dto);

        assertNotNull(result);
    }

    @Test
    void updateCreditorInstitution_ko() throws IOException {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.updateCreditorInstitutionDetails(anyString(), any(CreditorInstitutionDetails.class))).thenThrow(feignException);

        UpdateCreditorInstitutionDto dto = TestUtil.fileToObject("request/post_creditor_institution.json", UpdateCreditorInstitutionDto.class);
        assertThrows(FeignException.class, () -> service.updateCreditorInstitutionDetails(CI_TAX_CODE, dto));
    }

    @ParameterizedTest
    @CsvSource({
            "true,true",
            "true,false",
            "false,true"
    })
    void getBrokerAndEcDetails_ok(String existsBroker, String existsCI) throws IOException {
        FeignException feignException = mock(FeignException.NotFound.class);
        boolean isBrokerExistent = "true".equals(existsBroker);
        boolean isCIExistent = "true".equals(existsCI);
        String brokerCode = CI_TAX_CODE;

        if (isBrokerExistent) {
            when(apiConfigClient.getBrokersEC(1, 0, brokerCode, null, null, "ASC"))
                    .thenReturn(TestUtil.fileToObject("response/apiconfig/get_brokers_ok1.json", Brokers.class));
        } else {
            when(apiConfigClient.getBrokersEC(1, 0, brokerCode, null, null, "ASC")).thenThrow(feignException);
        }
        if (isCIExistent) {
            when(apiConfigClient.getCreditorInstitutionDetails(anyString()))
                    .thenReturn(TestUtil.fileToObject("response/apiconfig/get_creditor_institution_details_ok.json", CreditorInstitutionDetails.class));
        } else {
            when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenThrow(feignException);
        }

        BrokerAndEcDetailsResource result = service.getBrokerAndEcDetails(CI_TAX_CODE);

        assertNotNull(result);
        if (isBrokerExistent) {
            assertNotNull(result.getBrokerDetailsResource());
        } else {
            assertNull(result.getBrokerDetailsResource());
        }
        if (isCIExistent) {
            assertNotNull(result.getCreditorInstitutionDetailsResource());
        } else {
            assertNull(result.getCreditorInstitutionDetailsResource());
        }
    }

    @Test
    void getBrokerAndEcDetails_ko() {
        FeignException feignException = mock(FeignException.NotFound.class);
        when(apiConfigClient.getBrokersEC(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenThrow(feignException);
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenThrow(feignException);

        assertThrows(AppException.class, () -> service.getBrokerAndEcDetails(CI_TAX_CODE));
    }

    @Test
    void getCreditorInstitutionContactsSuccess() {
        TavoloOpEntity entity = buildTavoloOpEntity();
        InstitutionProductUsers users = buildInstitutionProductUsers();
        when(operativeTableRepository.findByTaxCode("ciTaxCode")).thenReturn(Optional.of(entity));
        when(externalApiClient.getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(Collections.singletonList(users));

        CreditorInstitutionContactsResource result = assertDoesNotThrow(() ->
                service.getCreditorInstitutionContacts("ciTaxCode", INSTITUTION_ID));

        assertNotNull(result);
        assertNotNull(result.getOperativeTable());
        assertEquals(entity.getName(), result.getOperativeTable().getName());
        assertEquals(entity.getEmail(), result.getOperativeTable().getEmail());
        assertEquals(entity.getTelephone(), result.getOperativeTable().getTelephone());
        assertNotNull(result.getCiPaymentContacts());
        assertEquals(1, result.getCiPaymentContacts().size());

        CIPaymentContact actualPaymentContact = result.getCiPaymentContacts().get(0);
        assertEquals(users.getEmail(), actualPaymentContact.getEmail());
        assertEquals(users.getName(), actualPaymentContact.getName());
        assertEquals(users.getSurname(), actualPaymentContact.getSurname());
        assertEquals(users.getFiscalCode(), actualPaymentContact.getFiscalCode());
    }

    @Test
    void getCreditorInstitutionContactsWithOperativeTableNotFound() {
        InstitutionProductUsers users = buildInstitutionProductUsers();
        when(operativeTableRepository.findByTaxCode("ciTaxCode")).thenReturn(Optional.empty());
        when(externalApiClient.getInstitutionProductUsers(
                INSTITUTION_ID,
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(Collections.singletonList(users));

        CreditorInstitutionContactsResource result = assertDoesNotThrow(() ->
                service.getCreditorInstitutionContacts("ciTaxCode", INSTITUTION_ID));

        assertNotNull(result);
        assertNull(result.getOperativeTable());
        assertNotNull(result.getCiPaymentContacts());
        assertEquals(1, result.getCiPaymentContacts().size());

        CIPaymentContact actualPaymentContact = result.getCiPaymentContacts().get(0);
        assertEquals(users.getEmail(), actualPaymentContact.getEmail());
        assertEquals(users.getName(), actualPaymentContact.getName());
        assertEquals(users.getSurname(), actualPaymentContact.getSurname());
        assertEquals(users.getFiscalCode(), actualPaymentContact.getFiscalCode());
    }

    @Test
    void getAvailableCreditorInstitutionsForStationSuccessWithoutAddingItselfToDelegationsAndPSPDelegationFiltered() {
        DelegationExternal expectedCI = buildDelegation("PA", CI_TAX_CODE_2);
        List<DelegationExternal> delegations = new ArrayList<>();
        delegations.add(buildDelegation("PSP", "12345678"));
        delegations.add(expectedCI);
        InstitutionResponse institutionResponse = buildInstitutionResponse(InstitutionType.PSP, "1234");

        when(externalApiClient.getBrokerDelegation(
                null,
                BROKER_ID,
                "prod-pagopa",
                "FULL",
                null)
        ).thenReturn(delegations);
        when(externalApiClient.getInstitution(BROKER_ID)).thenReturn(institutionResponse);
        when(apiConfigSelfcareIntegrationClient.getStationCreditorInstitutions(eq(STATION_CODE), anyList()))
                .thenReturn(Collections.singletonList(
                        CreditorInstitutionInfo.builder()
                                .ciTaxCode(expectedCI.getTaxCode())
                                .businessName(expectedCI.getInstitutionName())
                                .build())
                );

        CreditorInstitutionInfoResource result = assertDoesNotThrow(() ->
                service.getAvailableCreditorInstitutionsForStation(STATION_CODE, BROKER_ID, null));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionInfos());
        assertEquals(1, result.getCreditorInstitutionInfos().size());

        assertEquals(expectedCI.getInstitutionName(), result.getCreditorInstitutionInfos().get(0).getBusinessName());
        assertEquals(expectedCI.getTaxCode(), result.getCreditorInstitutionInfos().get(0).getCiTaxCode());
    }

    @Test
    void getAvailableCreditorInstitutionsForStationSuccessWithAddingItselfToDelegationsAndPSPDelegationFiltered() {
        DelegationExternal expectedCI = buildDelegation("PA", CI_TAX_CODE_2);
        List<DelegationExternal> delegations = new ArrayList<>();
        delegations.add(buildDelegation("PSP", "12345678"));
        delegations.add(expectedCI);
        InstitutionResponse institutionResponse = buildInstitutionResponse(InstitutionType.PA, "1234");

        when(externalApiClient.getBrokerDelegation(
                null,
                BROKER_ID,
                "prod-pagopa",
                "FULL",
                null)
        ).thenReturn(delegations);
        when(externalApiClient.getInstitution(BROKER_ID)).thenReturn(institutionResponse);
        when(apiConfigSelfcareIntegrationClient.getStationCreditorInstitutions(eq(STATION_CODE), anyList()))
                .thenReturn(List.of(
                        CreditorInstitutionInfo.builder()
                                .ciTaxCode(expectedCI.getTaxCode())
                                .businessName(expectedCI.getInstitutionName())
                                .build(),
                        CreditorInstitutionInfo.builder()
                                .ciTaxCode(institutionResponse.getTaxCode())
                                .businessName(institutionResponse.getDescription())
                                .build())
                );

        CreditorInstitutionInfoResource result = assertDoesNotThrow(() ->
                service.getAvailableCreditorInstitutionsForStation(STATION_CODE, BROKER_ID, null));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionInfos());
        assertEquals(2, result.getCreditorInstitutionInfos().size());
    }

    @Test
    void getAvailableCreditorInstitutionsForStationSuccessOnlyWithAddItselfToDelegations() {
        InstitutionResponse institutionResponse = buildInstitutionResponse(InstitutionType.PA, "1234");

        when(externalApiClient.getBrokerDelegation(
                null,
                BROKER_ID,
                "prod-pagopa",
                "FULL",
                null)
        ).thenReturn(new ArrayList<>());
        when(externalApiClient.getInstitution(BROKER_ID)).thenReturn(institutionResponse);
        when(apiConfigSelfcareIntegrationClient.getStationCreditorInstitutions(eq(STATION_CODE), anyList()))
                .thenReturn(Collections.singletonList(
                        CreditorInstitutionInfo.builder()
                                .ciTaxCode(institutionResponse.getTaxCode())
                                .businessName(institutionResponse.getDescription())
                                .build())
                );

        CreditorInstitutionInfoResource result = assertDoesNotThrow(() ->
                service.getAvailableCreditorInstitutionsForStation(STATION_CODE, BROKER_ID, null));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionInfos());
        assertEquals(1, result.getCreditorInstitutionInfos().size());

        assertEquals(institutionResponse.getDescription(), result.getCreditorInstitutionInfos().get(0).getBusinessName());
        assertEquals(institutionResponse.getTaxCode(), result.getCreditorInstitutionInfos().get(0).getCiTaxCode());
    }

    @Test
    void getAvailableCreditorInstitutionsForStationSuccessWithItselfAlreadyInDelegationShouldNotHaveDuplicatedInDelegations() {
        List<DelegationExternal> delegations = List.of(
                buildDelegation("SCP", CI_TAX_CODE_1),
                buildDelegation("PA", CI_TAX_CODE_2)
        );
        InstitutionResponse institutionResponse = buildInstitutionResponse(InstitutionType.SCP, CI_TAX_CODE_1);

        when(externalApiClient.getBrokerDelegation(
                null,
                BROKER_ID,
                "prod-pagopa",
                "FULL",
                null)
        ).thenReturn(delegations);
        when(externalApiClient.getInstitution(BROKER_ID)).thenReturn(institutionResponse);
        when(apiConfigSelfcareIntegrationClient.getStationCreditorInstitutions(eq(STATION_CODE), anyList()))
                .thenReturn(List.of(
                        CreditorInstitutionInfo.builder()
                                .ciTaxCode(delegations.get(0).getTaxCode())
                                .businessName(delegations.get(0).getInstitutionName())
                                .build(),
                        CreditorInstitutionInfo.builder()
                                .ciTaxCode(delegations.get(1).getTaxCode())
                                .businessName(delegations.get(1).getInstitutionName())
                                .build())
                );

        CreditorInstitutionInfoResource result = assertDoesNotThrow(() ->
                service.getAvailableCreditorInstitutionsForStation(STATION_CODE, BROKER_ID, null));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionInfos());
        assertEquals(2, result.getCreditorInstitutionInfos().size());
    }

    @Test
    void getAvailableCreditorInstitutionsForStationSuccessWithFilterNoResults() {
        InstitutionResponse institutionResponse = buildInstitutionResponse(InstitutionType.SCP, CI_TAX_CODE_1);

        when(externalApiClient.getBrokerDelegation(
                null,
                BROKER_ID,
                "prod-pagopa",
                "FULL",
                "EC")
        ).thenReturn(new ArrayList<>());
        when(externalApiClient.getInstitution(BROKER_ID)).thenReturn(institutionResponse);

        CreditorInstitutionInfoResource result = assertDoesNotThrow(() ->
                service.getAvailableCreditorInstitutionsForStation(STATION_CODE, BROKER_ID, "EC"));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionInfos());
        assertTrue(result.getCreditorInstitutionInfos().isEmpty());

        verify(apiConfigSelfcareIntegrationClient, never()).getStationCreditorInstitutions(eq(STATION_CODE), anyList());
    }

    @Test
    void getAvailableCreditorInstitutionsForStationSuccessWithNoCIAvailable() {
        List<DelegationExternal> delegations = List.of(
                buildDelegation("SCP", CI_TAX_CODE_1),
                buildDelegation("PA", CI_TAX_CODE_2)
        );
        InstitutionResponse institutionResponse = buildInstitutionResponse(InstitutionType.SCP, CI_TAX_CODE_1);

        when(externalApiClient.getBrokerDelegation(
                null,
                BROKER_ID,
                "prod-pagopa",
                "FULL",
                null)
        ).thenReturn(delegations);
        when(externalApiClient.getInstitution(BROKER_ID)).thenReturn(institutionResponse);
        when(apiConfigSelfcareIntegrationClient.getStationCreditorInstitutions(eq(STATION_CODE), anyList()))
                .thenReturn(Collections.emptyList());

        CreditorInstitutionInfoResource result = assertDoesNotThrow(() ->
                service.getAvailableCreditorInstitutionsForStation(STATION_CODE, BROKER_ID, null));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionInfos());
        assertTrue(result.getCreditorInstitutionInfos().isEmpty());
    }

    @Test
    void getAvailableCreditorInstitutionsForStationSuccessAllPSPDelegations() {
        List<DelegationExternal> delegations = List.of(
                buildDelegation("PSP", CI_TAX_CODE_1),
                buildDelegation("PSP", CI_TAX_CODE_2)
        );
        InstitutionResponse institutionResponse = buildInstitutionResponse(InstitutionType.PSP, CI_TAX_CODE_1);

        when(externalApiClient.getBrokerDelegation(
                null,
                BROKER_ID,
                "prod-pagopa",
                "FULL",
                null)
        ).thenReturn(delegations);
        when(externalApiClient.getInstitution(BROKER_ID)).thenReturn(institutionResponse);

        CreditorInstitutionInfoResource result = assertDoesNotThrow(() ->
                service.getAvailableCreditorInstitutionsForStation(STATION_CODE, BROKER_ID, null));

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionInfos());
        assertTrue(result.getCreditorInstitutionInfos().isEmpty());

        verify(apiConfigSelfcareIntegrationClient, never()).getStationCreditorInstitutions(eq(STATION_CODE), anyList());
    }

    private TavoloOpEntity buildTavoloOpEntity() {
        TavoloOpEntity entity = new TavoloOpEntity();
        entity.setName("Name");
        entity.setEmail("email");
        entity.setTelephone("12234545");
        return entity;
    }

    private InstitutionProductUsers buildInstitutionProductUsers() {
        return InstitutionProductUsers.builder()
                .fiscalCode("taxCode")
                .email("emailUser")
                .name("name")
                .surname("surname")
                .build();
    }

    private DelegationExternal buildDelegation(String institutionType, String taxCode) {
        return DelegationExternal
                .builder()
                .id(UUID.randomUUID().toString())
                .brokerId("00001")
                .brokerName("BrokerPsp")
                .brokerTaxCode("000001")
                .brokerType("TypePSP")
                .institutionId("0001")
                .institutionName("Institution Psp " + UUID.randomUUID())
                .institutionRootName("Institution Root Name Psp 1")
                .institutionType(institutionType)
                .taxCode(taxCode)
                .build();
    }

    private InstitutionResponse buildInstitutionResponse(InstitutionType institutionType, String taxCode) {
        return InstitutionResponse.builder().description("Broker").taxCode(taxCode).institutionType(institutionType).build();
    }

    private CreditorInstitutions buildCreditorInstitutions() {
        return CreditorInstitutions.builder()
                .creditorInstitutionList(Collections.singletonList(
                        CreditorInstitution.builder()
                                .aca(true)
                                .standIn(true)
                                .creditorInstitutionCode(CI_TAX_CODE)
                                .mod4(false)
                                .applicationCode(null)
                                .segregationCode(2L)
                                .auxDigit(null)
                                .broadcast(false)
                                .enabled(true)
                                .cbillCode("cbill")
                                .build()
                ))
                .build();
    }
}
