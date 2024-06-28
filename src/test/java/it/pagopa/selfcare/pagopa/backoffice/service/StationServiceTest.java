package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ForwarderClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStation;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.WrapperStationList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestResultEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationsResource;
import kong.unirest.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StationService.class})
class StationServiceTest {

    private static final String STATION_CODE = "stationCode";
    private static final String BROKER_CODE = "brokerCode";
    private static final int LIMIT = 10;
    private static final int PAGE = 0;
    private static final String EC_CODE = "ecCode";
    private static final String SORTING_DESC = "DESC";
    private static final String SORTING_ASC = "ASC";

    @Autowired
    private StationService service;

    @MockBean
    private WrapperService wrapperService;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private AwsSesClient awsSesClient;

    @MockBean
    private ForwarderClient forwarderClient;

    @MockBean
    private JiraServiceManagerClient jiraServiceManagerClient;

    @Test
    void createStationSuccess() {
        WrapperEntity<StationDetails> entity = new WrapperEntity<>();
        WrapperEntities<StationDetails> entities = new WrapperEntities<>();
        entities.setEntities(Collections.singletonList(entity));

        when(wrapperService.updateValidatedWrapperStation(any(StationDetails.class), any()))
                .thenReturn(buildWrapperEntityStation(WrapperStatus.TO_CHECK));

        StationDetailResource result = assertDoesNotThrow(() -> service.createStation(buildStationDetailsDto()));

        assertNotNull(result);

        verify(apiConfigClient).createStation(any());
        verify(awsSesClient).sendEmail(any());
    }

    @Test
    void createWrapperStationDetailsSuccess() {
        WrapperEntity<StationDetails> entity = new WrapperEntity<>();
        WrapperEntities<StationDetails> entities = new WrapperEntities<>();
        entities.setEntities(Collections.singletonList(entity));

        when(wrapperService.createWrapperStation(any(StationDetails.class), any()))
                .thenReturn(entities);

        WrapperEntities<StationDetails> result =
                assertDoesNotThrow(() -> service.createWrapperStationDetails(buildWrapperStationDetailsDto()));

        assertNotNull(result);

        verify(jiraServiceManagerClient).createTicket(anyString(), anyString());
    }

    @Test
    void getStationsActiveSuccessWithWrapperFound() {
        Stations stations = buildStations(STATION_CODE);

        when(apiConfigClient.getStations(LIMIT, PAGE, SORTING_DESC, BROKER_CODE, null, STATION_CODE))
                .thenReturn(stations);
        when(wrapperService.findStationByIdOptional(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStation(WrapperStatus.TO_CHECK)));

        WrapperStationsResource result =
                assertDoesNotThrow(() -> service.getStations(ConfigurationStatus.ACTIVE, STATION_CODE, BROKER_CODE, LIMIT, PAGE));

        assertNotNull(result);
        assertNotNull(result.getStationsList());
        assertEquals(1, result.getStationsList().size());
        assertEquals(STATION_CODE, result.getStationsList().get(0).getStationCode());
        assertEquals(true, result.getStationsList().get(0).getEnabled());
        assertEquals(1L, result.getStationsList().get(0).getVersion());
    }

    @Test
    void getStationsActiveSuccessWithWrapperNotFound() {
        Stations stations = buildStations(STATION_CODE);

        when(apiConfigClient.getStations(LIMIT, PAGE, SORTING_DESC, BROKER_CODE, null, STATION_CODE))
                .thenReturn(stations);
        when(wrapperService.findStationByIdOptional(STATION_CODE)).thenReturn(Optional.empty());

        WrapperStationsResource result =
                assertDoesNotThrow(() -> service.getStations(ConfigurationStatus.ACTIVE, STATION_CODE, BROKER_CODE, LIMIT, PAGE));

        assertNotNull(result);
        assertNotNull(result.getStationsList());
        assertEquals(1, result.getStationsList().size());
        assertEquals(STATION_CODE, result.getStationsList().get(0).getStationCode());
        assertEquals(true, result.getStationsList().get(0).getEnabled());
        assertEquals(1L, result.getStationsList().get(0).getVersion());
    }

    @Test
    void getStationsToBeValidatedSuccess() {
        var wrapperEntitiesList = buildWrapperStationList();

        when(wrapperService.getWrapperStations(STATION_CODE, BROKER_CODE, LIMIT, PAGE))
                .thenReturn(wrapperEntitiesList);

        WrapperStationsResource result =
                assertDoesNotThrow(() -> service.getStations(ConfigurationStatus.TO_BE_VALIDATED, STATION_CODE, BROKER_CODE, LIMIT, PAGE));

        assertNotNull(result);

        assertNotNull(result.getStationsList());
        assertEquals(1, result.getStationsList().size());
        assertEquals(STATION_CODE, result.getStationsList().get(0).getStationCode());
        assertEquals(true, result.getStationsList().get(0).getEnabled());
        assertEquals(1L, result.getStationsList().get(0).getVersion());
    }

    @Test
    void getStationDetailSuccessFromWrapper() {
        when(wrapperService.findStationById(STATION_CODE)).thenReturn(buildWrapperEntityStation(WrapperStatus.TO_CHECK));

        StationDetailResource result = assertDoesNotThrow(() -> service.getStationDetail(STATION_CODE, ConfigurationStatus.TO_BE_VALIDATED));

        assertNotNull(result);

        assertEquals(STATION_CODE, result.getStationCode());
        assertTrue(result.getEnabled());
        assertEquals(1L, result.getVersion());

        verify(apiConfigClient, never()).getStation(STATION_CODE);
        verify(wrapperService, never()).findStationByIdOptional(STATION_CODE);
    }

    @Test
    void getStationDetailSuccessFromApiConfigWithNoPendingUpdateNoWrapperFound() {
        when(apiConfigClient.getStation(STATION_CODE)).thenReturn(buildStationDetails());
        when(wrapperService.findStationByIdOptional(STATION_CODE)).thenReturn(Optional.empty());

        StationDetailResource result = assertDoesNotThrow(() -> service.getStationDetail(STATION_CODE, ConfigurationStatus.ACTIVE));

        assertNotNull(result);

        assertEquals(STATION_CODE, result.getStationCode());
        assertTrue(result.getEnabled());
        assertEquals(1L, result.getVersion());
        assertFalse(result.getPendingUpdate());
    }

    @Test
    void getStationDetailSuccessFromApiConfigWithNoPendingUpdate() {
        when(apiConfigClient.getStation(STATION_CODE)).thenReturn(buildStationDetails());
        when(wrapperService.findStationByIdOptional(STATION_CODE))
                .thenReturn(Optional.of(buildWrapperEntityStation(WrapperStatus.APPROVED)));

        StationDetailResource result = assertDoesNotThrow(() -> service.getStationDetail(STATION_CODE, ConfigurationStatus.ACTIVE));

        assertNotNull(result);

        assertEquals(STATION_CODE, result.getStationCode());
        assertTrue(result.getEnabled());
        assertEquals(1L, result.getVersion());
        assertFalse(result.getPendingUpdate());
    }

    @Test
    void getStationDetailSuccessFromApiConfigWithPendingUpdate() {
        when(apiConfigClient.getStation(STATION_CODE)).thenReturn(buildStationDetails());
        when(wrapperService.findStationByIdOptional(STATION_CODE))
                .thenReturn(Optional.of(buildWrapperEntityStation(WrapperStatus.TO_CHECK)));

        StationDetailResource result = assertDoesNotThrow(() -> service.getStationDetail(STATION_CODE, ConfigurationStatus.ACTIVE));

        assertNotNull(result);

        assertEquals(STATION_CODE, result.getStationCode());
        assertTrue(result.getEnabled());
        assertEquals(1L, result.getVersion());
        assertTrue(result.getPendingUpdate());
    }

    @Test
    void getStationCodeV1Success() {
        WrapperEntitiesList emptyEntitiesList = new WrapperEntitiesList();
        emptyEntitiesList.setWrapperEntities(Collections.emptyList());

        when(wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(
                WrapperStatus.TO_CHECK,
                WrapperType.STATION,
                null,
                EC_CODE,
                0,
                1,
                SORTING_ASC)
        ).thenReturn(emptyEntitiesList);
        when(wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(
                WrapperStatus.TO_FIX,
                WrapperType.STATION,
                null,
                EC_CODE,
                0,
                1,
                SORTING_ASC)
        ).thenReturn(emptyEntitiesList);
        when(apiConfigClient.getStations(100, 0, SORTING_ASC, null, null, EC_CODE))
                .thenReturn(buildStations(STATION_CODE));

        StationCodeResource result = assertDoesNotThrow(() -> service.getStationCode(EC_CODE, false));

        assertNotNull(result);

        assertEquals(String.format("%s_01", EC_CODE), result.getStationCode());

        verify(wrapperService, never()).getFirstValidStationCodeV2(EC_CODE);
    }

    @Test
    void getStationCodeV1FailConflict() {
        WrapperEntitiesList emptyEntitiesList = new WrapperEntitiesList();
        emptyEntitiesList.setWrapperEntities(Collections.emptyList());

        when(wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(
                WrapperStatus.TO_CHECK,
                WrapperType.STATION,
                null,
                EC_CODE,
                0,
                1,
                SORTING_ASC)
        ).thenReturn(buildWrapperEntitiesList());
        when(wrapperService.findByStatusAndTypeAndBrokerCodeAndIdLike(
                WrapperStatus.TO_FIX,
                WrapperType.STATION,
                null,
                EC_CODE,
                0,
                1,
                SORTING_ASC)
        ).thenReturn(emptyEntitiesList);

        AppException e = assertThrows(AppException.class, () -> service.getStationCode(EC_CODE, false));

        assertNotNull(e);

        assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());

        verify(wrapperService, never()).getFirstValidStationCodeV2(EC_CODE);
        verify(apiConfigClient, never()).getStations(100, 0, SORTING_ASC, null, null, EC_CODE);
    }

    @Test
    void getStationCodeV2Success() {
        when(wrapperService.getFirstValidStationCodeV2(EC_CODE)).thenReturn(STATION_CODE);

        StationCodeResource result = assertDoesNotThrow(() -> service.getStationCode(EC_CODE, true));

        assertNotNull(result);

        assertEquals(STATION_CODE, result.getStationCode());
    }

    @Test
    void updateWrapperStationDetailsSuccess() {
        when(wrapperService.updateWrapperStation(anyString(), any())).thenReturn(buildWrapperEntityStation(WrapperStatus.TO_CHECK));

        StationDetailResource result = assertDoesNotThrow(() -> service.updateWrapperStationDetails(STATION_CODE, buildStationDetailsDto()));

        assertNotNull(result);

        verify(jiraServiceManagerClient).createTicket(anyString(), anyString());
    }

    @Test
    void updateWrapperStationWithOperatorReviewSuccess() {
        when(wrapperService.updateStationWithOperatorReview(anyString(), anyString()))
                .thenReturn(buildWrapperEntityStation(WrapperStatus.TO_CHECK));

        StationDetailResource result = assertDoesNotThrow(() -> service.updateWrapperStationWithOperatorReview(STATION_CODE, BROKER_CODE, "nota"));

        assertNotNull(result);

        verify(awsSesClient).sendEmail(any());
    }

    @Test
    void updateWrapperStationWithOperatorReviewFail() {
        when(wrapperService.updateStationWithOperatorReview(anyString(), anyString()))
                .thenThrow(AppException.class);

        AppException e = assertThrows(AppException.class,
                () -> service.updateWrapperStationWithOperatorReview(STATION_CODE, BROKER_CODE, "nota"));

        assertNotNull(e);

        verify(awsSesClient, never()).sendEmail(any());
    }

    @Test
    void getCreditorInstitutionsByStationCodeSuccess() {
        CreditorInstitutions institutions = buildCreditorInstitutions();

        when(apiConfigClient.getCreditorInstitutionsByStation(STATION_CODE, LIMIT, PAGE, "ciNameOrFiscalCode"))
                .thenReturn(institutions);

        CreditorInstitutionsResource result = assertDoesNotThrow(() ->
                service.getCreditorInstitutionsByStationCode(STATION_CODE, LIMIT, PAGE, "ciNameOrFiscalCode")
        );

        assertNotNull(result);
        assertNotNull(result.getCreditorInstitutionList());
        assertEquals(1, result.getCreditorInstitutionList().size());

        CreditorInstitution expectedCI = institutions.getCreditorInstitutionList().get(0);
        CreditorInstitutionResource actualCI = result.getCreditorInstitutionList().get(0);
        assertEquals(expectedCI.getCreditorInstitutionCode(), actualCI.getCiTaxCode());
        assertEquals(expectedCI.getEnabled(), actualCI.getEnabled());
        assertEquals(expectedCI.getBusinessName(), actualCI.getBusinessName());
    }

    @Test
    void updateStationSuccess() {
        StationDetails stationDetails = buildStationDetails();
        when(apiConfigClient.updateStation(eq(STATION_CODE), any())).thenReturn(stationDetails);

        StationDetailResource result = assertDoesNotThrow(() -> service.updateStation(buildStationDetailsDto(), STATION_CODE));

        assertNotNull(result);

        assertEquals(stationDetails.getStationCode(), result.getStationCode());
        assertEquals(stationDetails.getEnabled(), result.getEnabled());
        assertEquals(stationDetails.getVersion(), result.getVersion());

        verify(wrapperService).update(any(StationDetails.class), anyString(), anyString(), eq(null));
        verify(awsSesClient).sendEmail(any());
    }

    @Test
    void testStationShouldReturnSuccessOnValidForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(200);
        when(forwarderClient.testForwardConnection(any(), any(), any(), any(), any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.SUCCESS, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(), any(), any(), any(), any());
    }

    @Test
    void testStationShouldReturnCertErrorOnCertErrorForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(401);
        when(forwarderClient.testForwardConnection(any(), any(), any(), any(), any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.CERTIFICATE_ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(), any(), any(), any(), any());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 500})
    void testStationShouldReturnErrorOnNotFoundForwardCall(int httResponseStatus) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(httResponseStatus);
        when(forwarderClient.testForwardConnection(any(), any(), any(), any(), any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(), any(), any(), any(), any());
    }

    private StationDetailsDto buildStationDetailsDto() {
        StationDetailsDto dto = new StationDetailsDto();
        dto.setStationCode(STATION_CODE);
        dto.setVersion(1L);
        dto.setBrokerCode(BROKER_CODE);
        dto.setPrimitiveVersion(1);
        return dto;
    }

    private WrapperStationDetailsDto buildWrapperStationDetailsDto() {
        WrapperStationDetailsDto dto = new WrapperStationDetailsDto();
        dto.setPrimitiveVersion(1);
        dto.setStationCode(STATION_CODE);
        return dto;
    }

    private WrapperEntitiesList buildWrapperEntitiesList() {
        WrapperEntities<StationDetails> entities = buildStationDetailsWrapperEntities();
        WrapperEntitiesList wrapperEntitiesList = new WrapperEntitiesList();
        wrapperEntitiesList.setWrapperEntities(Collections.singletonList(entities));
        wrapperEntitiesList.setPageInfo(PageInfo.builder()
                .itemsFound(1)
                .totalPages(1)
                .limit(LIMIT)
                .page(PAGE)
                .totalItems(1L)
                .build());
        return wrapperEntitiesList;
    }

    private WrapperStationList buildWrapperStationList() {
        var entities = buildWrapperEntityStation(WrapperStatus.TO_CHECK);
        WrapperStationList wrapperEntitiesList = new WrapperStationList();
        wrapperEntitiesList.setWrapperEntities(Collections.singletonList(entities));
        wrapperEntitiesList.setPageInfo(PageInfo.builder()
                .itemsFound(1)
                .totalPages(1)
                .limit(LIMIT)
                .page(PAGE)
                .totalItems(1L)
                .build());
        return wrapperEntitiesList;
    }

    private WrapperEntities<StationDetails> buildStationDetailsWrapperEntities() {
        WrapperEntity<StationDetails> entity = new WrapperEntity<>();
        entity.setEntity(buildStationDetails());
        WrapperEntities<StationDetails> entities = new WrapperEntities<>();
        entities.setCreatedAt(Instant.now());
        entities.setEntities(Collections.singletonList(entity));
        return entities;
    }

    private WrapperEntityStations buildWrapperEntityStation(WrapperStatus wrapperStatus) {
        WrapperEntityStation entity = new WrapperEntityStation();
        entity.setEntity(buildStationDetails());
        entity.setStatus(wrapperStatus);
        WrapperEntityStations entities = new WrapperEntityStations();
        entities.setCreatedAt(Instant.now());
        entities.setEntities(Collections.singletonList(entity));
        return entities;
    }

    private StationDetails buildStationDetails() {
        StationDetails stationDetails = new StationDetails();
        stationDetails.setStationCode(STATION_CODE);
        stationDetails.setEnabled(true);
        stationDetails.setVersion(1L);
        stationDetails.setService("service");
        return stationDetails;
    }

    private Stations buildStations(String stationCode) {
        Station station = new Station();
        station.setStationCode(stationCode);
        station.setEnabled(true);
        station.setVersion(1L);
        Stations stations = new Stations();
        stations.setStationsList(Collections.singletonList(station));
        stations.setPageInfo(PageInfo.builder()
                .itemsFound(1)
                .totalPages(1)
                .limit(LIMIT)
                .page(PAGE)
                .totalItems(1L)
                .build());
        return stations;
    }

    private @NotNull CreditorInstitutions buildCreditorInstitutions() {
        CreditorInstitutions institutions = new CreditorInstitutions();
        CreditorInstitution creditorInstitution = new CreditorInstitution();
        creditorInstitution.setCreditorInstitutionCode("creditorInstitutionCode");
        creditorInstitution.setEnabled(true);
        creditorInstitution.setBusinessName("businessName");
        institutions.setCreditorInstitutionList(Collections.singletonList(creditorInstitution));
        return institutions;
    }
}
