package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StationMaintenanceService.class})
class StationMaintenanceServiceTest {

    private static final String STATION_CODE = "stationCode";
    private static final String BROKER_CODE = "brokerCode";
    private static final long MAINTENANCE_ID = 100;
    @Autowired
    private StationMaintenanceService stationMaintenanceService;
    @MockBean
    private ApiConfigClient apiConfigClient;

    @Test
    void createStationMaintenanceSuccess() {
        StationMaintenanceResource response = new StationMaintenanceResource();
        response.setStationCode(STATION_CODE);
        response.setStandIn(true);
        response.setEndDateTime(OffsetDateTime.now());
        response.setStartDateTime(OffsetDateTime.now());
        response.setMaintenanceId(MAINTENANCE_ID);
        response.setBrokerCode(BROKER_CODE);

        when(apiConfigClient.createStationMaintenance(anyString(), any(CreateStationMaintenance.class)))
                .thenReturn(response);

        CreateStationMaintenance request = new CreateStationMaintenance();
        request.setStationCode(STATION_CODE);
        request.setStandIn(true);
        request.setEndDateTime(OffsetDateTime.now());
        request.setStartDateTime(OffsetDateTime.now());
        StationMaintenanceResource result = assertDoesNotThrow(() -> stationMaintenanceService.createStationMaintenance(BROKER_CODE, request));

        assertNotNull(result);

        verify(apiConfigClient).createStationMaintenance(anyString(), any(CreateStationMaintenance.class));
    }

    @Test
    void finishStationMaintenanceSuccess() {
        when(apiConfigClient.getStationMaintenance(BROKER_CODE, MAINTENANCE_ID))
                .thenReturn(buildMaintenanceResource(
                        OffsetDateTime.now().minusHours(1).truncatedTo(ChronoUnit.MINUTES),
                        OffsetDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES)
                ));

        assertDoesNotThrow(() -> stationMaintenanceService.finishStationMaintenance(BROKER_CODE, MAINTENANCE_ID));

        verify(apiConfigClient).updateStationMaintenance(anyString(), anyLong(), any());
    }

    @Test
    void finishStationMaintenanceFailMaintenanceNotStarted() {
        when(apiConfigClient.getStationMaintenance(BROKER_CODE, MAINTENANCE_ID))
                .thenReturn(buildMaintenanceResource(
                        OffsetDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES),
                        OffsetDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES)
                ));

        AppException e = assertThrows(AppException.class, () ->
                stationMaintenanceService.finishStationMaintenance(BROKER_CODE, MAINTENANCE_ID));

        assertNotNull(e);
        assertEquals(AppError.STATION_MAINTENANCE_NOT_IN_PROGRESS.title, e.getTitle());
        assertEquals(AppError.STATION_MAINTENANCE_NOT_IN_PROGRESS.httpStatus, e.getHttpStatus());

        verify(apiConfigClient, never()).updateStationMaintenance(anyString(), anyLong(), any());
    }

    @Test
    void finishStationMaintenanceFailMaintenanceAlreadyTerminated() {
        when(apiConfigClient.getStationMaintenance(BROKER_CODE, MAINTENANCE_ID))
                .thenReturn(buildMaintenanceResource(
                        OffsetDateTime.now().minusHours(2).truncatedTo(ChronoUnit.MINUTES),
                        OffsetDateTime.now().minusHours(1).truncatedTo(ChronoUnit.MINUTES)
                ));

        AppException e = assertThrows(AppException.class, () ->
                stationMaintenanceService.finishStationMaintenance(BROKER_CODE, MAINTENANCE_ID));

        assertNotNull(e);
        assertEquals(AppError.STATION_MAINTENANCE_NOT_IN_PROGRESS.title, e.getTitle());
        assertEquals(AppError.STATION_MAINTENANCE_NOT_IN_PROGRESS.httpStatus, e.getHttpStatus());

        verify(apiConfigClient, never()).updateStationMaintenance(anyString(), anyLong(), any());
    }

    private StationMaintenanceResource buildMaintenanceResource(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        return StationMaintenanceResource.builder()
                .maintenanceId(123L)
                .stationCode(STATION_CODE)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .standIn(true)
                .brokerCode(BROKER_CODE)
                .build();
    }
}
