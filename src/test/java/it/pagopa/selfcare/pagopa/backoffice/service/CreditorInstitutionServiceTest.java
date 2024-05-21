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
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.AvailableCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CIPaymentContact;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionAndBrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionContactsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionStationEditResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.UpdateCreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.repository.TavoloOpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CreditorInstitutionService.class, MappingsConfiguration.class})
class CreditorInstitutionServiceTest {

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @MockBean
    private TavoloOpRepository operativeTableRepository;

    @MockBean
    private ExternalApiClient externalApiClient;

    @Autowired
    private CreditorInstitutionService service;

    @Test
    void getCreditorInstitutions_ok() throws IOException {
        when(apiConfigClient.getCreditorInstitutions(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/get_creditor_institutions_ok.json", CreditorInstitutions.class));

        CreditorInstitutionsResource result = service.getCreditorInstitutions(50, 0, "12345", "comune", "asc");

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionList());
        assertFalse(result.getCreditorInstitutionList().isEmpty());
    }

    @Test
    void getCreditorInstitutions_ko() {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.getCreditorInstitutions(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenThrow(feignException);

        assertThrows(FeignException.class, () -> service.getCreditorInstitutions(50, 0, "12345", "comune", "asc"));
    }

    @Test
    void getCreditorInstitutionDetails_ok() throws IOException {
        when(apiConfigClient.getCreditorInstitutionDetails(anyString()))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/get_creditor_institution_details_ok.json", CreditorInstitutionDetails.class));

        CreditorInstitutionDetailsResource result = service.getCreditorInstitutionDetails("12345678900");

        assertNotNull(result);
    }

    @Test
    void getCreditorInstitutionDetails_ko() {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenThrow(feignException);

        assertThrows(FeignException.class, () -> service.getCreditorInstitutionDetails("12345678900"));
    }

    @Test
    void getCreditorInstitutionSegregationCodes_ok() {
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionSegregationCodes(anyString()))
                .thenReturn(AvailableCodes.builder().availableCodeList(Collections.singletonList("2")).build());

        AvailableCodes result = assertDoesNotThrow(() -> service.getCreditorInstitutionSegregationCodes("12345678900"));

        assertNotNull(result);
    }

    @Test
    void getCreditorInstitutionSegregationCodes_ko() {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionSegregationCodes(anyString())).thenThrow(feignException);

        assertThrows(FeignException.class, () -> service.getCreditorInstitutionSegregationCodes("12345678900"));
    }

    @Test
    void associateStationToCreditorInstitution_ok() throws IOException {
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenReturn(new CreditorInstitutionDetails());
        when(apiConfigClient.createCreditorInstitutionStationRelationship(anyString(), any(CreditorInstitutionStationEdit.class)))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/post_creditor_institution_station_association_ok.json", CreditorInstitutionStationEdit.class));

        CreditorInstitutionStationDto dto = TestUtil.fileToObject("request/post_creditor_institution_station_association.json", CreditorInstitutionStationDto.class);
        CreditorInstitutionStationEditResource result = service.associateStationToCreditorInstitution("12345678900", dto);

        assertNotNull(result);
    }

    @Test
    void associateStationToCreditorInstitution_ko() throws IOException {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.getCreditorInstitutionDetails(anyString())).thenThrow(feignException);

        CreditorInstitutionStationDto dto = TestUtil.fileToObject("request/post_creditor_institution_station_association.json", CreditorInstitutionStationDto.class);
        assertThrows(AppException.class, () -> service.associateStationToCreditorInstitution("12345678900", dto));
        verify(apiConfigClient, times(0)).createCreditorInstitutionStationRelationship(anyString(), any(CreditorInstitutionStationEdit.class));
    }

    @Test
    void deleteCreditorInstitutionStationRelationship_ok() {
        doNothing().when(apiConfigClient).deleteCreditorInstitutionStationRelationship(anyString(), anyString());

        assertDoesNotThrow(() -> service.deleteCreditorInstitutionStationRelationship("12345678900", "00000000000_01"));
    }

    @Test
    void deleteCreditorInstitutionStationRelationship_ko() {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        doThrow(feignException).when(apiConfigClient).deleteCreditorInstitutionStationRelationship(anyString(), anyString());

        assertThrows(FeignException.class, () -> service.deleteCreditorInstitutionStationRelationship("12345678900", "00000000000_01"));
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
        CreditorInstitutionDetailsResource result = service.updateCreditorInstitutionDetails("12345678900", dto);

        assertNotNull(result);
    }

    @Test
    void updateCreditorInstitution_ko() throws IOException {
        FeignException feignException = mock(FeignException.InternalServerError.class);
        when(apiConfigClient.updateCreditorInstitutionDetails(anyString(), any(CreditorInstitutionDetails.class))).thenThrow(feignException);

        UpdateCreditorInstitutionDto dto = TestUtil.fileToObject("request/post_creditor_institution.json", UpdateCreditorInstitutionDto.class);
        assertThrows(FeignException.class, () -> service.updateCreditorInstitutionDetails("12345678900", dto));
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
        String brokerCode = "12345678900";

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

        BrokerAndEcDetailsResource result = service.getBrokerAndEcDetails("12345678900");

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

        assertThrows(AppException.class, () -> service.getBrokerAndEcDetails("12345678900"));
    }

    @Test
    void getCreditorInstitutionContactsSuccess() {
        TavoloOpEntity entity = buildTavoloOpEntity();
        InstitutionProductUsers users = buildInstitutionProductUsers();
        when(operativeTableRepository.findByTaxCode("ciTaxCode")).thenReturn(Optional.of(entity));
        when(externalApiClient.getInstitutionProductUsers(
                "institutionId",
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(Collections.singletonList(users));

        CreditorInstitutionContactsResource result = assertDoesNotThrow(() ->
                service.getCreditorInstitutionContacts("ciTaxCode", "institutionId"));

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
                "institutionId",
                null,
                null,
                Collections.singletonList(SelfcareProductUser.ADMIN.getProductUser()))
        ).thenReturn(Collections.singletonList(users));

        CreditorInstitutionContactsResource result = assertDoesNotThrow(() ->
                service.getCreditorInstitutionContacts("ciTaxCode", "institutionId"));

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
}
