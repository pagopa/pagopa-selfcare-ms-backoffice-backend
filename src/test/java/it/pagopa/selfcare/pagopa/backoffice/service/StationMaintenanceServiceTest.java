package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
}
