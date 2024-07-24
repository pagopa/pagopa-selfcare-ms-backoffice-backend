package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StationMaintenanceService.class})
class StationMaintenanceServiceTest {

    private final static String STATION_CODE = "stationCode";
    private final static String BROKER_CODE = "brokerCode";
    private final static long MAINTENANCE_ID = 100;
    @Autowired
    private StationMaintenanceService stationMaintenanceService;
    @MockBean
    private ApiConfigClient apiConfigClient;

    @Test
    void createStationMaintenanceSuccess() {
        StationMaintenanceResource response = buildMaintenanceResource();

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
    void getStationMaintenanceDetailSuccess() {
        StationMaintenanceResource mockedResult = buildMaintenanceResource();
        when(apiConfigClient.getStationMaintenance(any(),any())).thenReturn(mockedResult);
        StationMaintenanceResource resource =
            stationMaintenanceService.getStationMaintenance("brokerCode",1L);
        assertNotNull(resource);
        assertEquals(mockedResult, resource);
    }

    StationMaintenanceResource buildMaintenanceResource() {
        StationMaintenanceResource resource = new StationMaintenanceResource();
        resource.setStationCode(STATION_CODE);
        resource.setStandIn(true);
        resource.setEndDateTime(OffsetDateTime.now());
        resource.setStartDateTime(OffsetDateTime.now());
        resource.setMaintenanceId(MAINTENANCE_ID);
        resource.setBrokerCode(BROKER_CODE);
        return resource;
    }

}
