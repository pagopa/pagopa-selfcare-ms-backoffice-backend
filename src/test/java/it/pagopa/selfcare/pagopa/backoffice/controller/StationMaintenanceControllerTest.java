package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.MaintenanceHoursSummaryResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListState;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.UpdateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.service.StationMaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class StationMaintenanceControllerTest {

    private final static String STATION_CODE = "stationCode";
    private final static String BROKER_CODE = "brokerCode";
    private final static long MAINTENANCE_ID = 100;

    @MockBean
    private StationMaintenanceService stationMaintenanceService;
    @Autowired
    private MockMvc mvc;
    @Inject
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(stationMaintenanceService);
    }

    @Test
    void createStationMaintenance() throws Exception {
        StationMaintenanceResource response = new StationMaintenanceResource();
        response.setStationCode(STATION_CODE);
        response.setStandIn(true);
        response.setEndDateTime(OffsetDateTime.now());
        response.setStartDateTime(OffsetDateTime.now());
        response.setMaintenanceId(MAINTENANCE_ID);
        response.setBrokerCode(BROKER_CODE);
        when(stationMaintenanceService.createStationMaintenance(anyString(), any())).thenReturn(response);

        CreateStationMaintenance request = new CreateStationMaintenance();
        request.setStationCode(STATION_CODE);
        request.setStandIn(true);
        request.setEndDateTime(OffsetDateTime.now());
        request.setStartDateTime(OffsetDateTime.now());
        mvc.perform(post("/brokers/{brokercode}/station-maintenances", BROKER_CODE)
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void updateStationMaintenance() throws Exception {
        StationMaintenanceResource response = new StationMaintenanceResource();
        response.setStationCode(STATION_CODE);
        response.setStandIn(true);
        response.setEndDateTime(OffsetDateTime.now());
        response.setStartDateTime(OffsetDateTime.now());
        response.setMaintenanceId(MAINTENANCE_ID);
        response.setBrokerCode(BROKER_CODE);
        when(stationMaintenanceService.updateStationMaintenance(anyString(), anyLong(), any())).thenReturn(response);

        UpdateStationMaintenance request = new UpdateStationMaintenance();
        request.setStandIn(true);
        request.setEndDateTime(OffsetDateTime.now());
        request.setStartDateTime(OffsetDateTime.now());
        mvc.perform(put("/brokers/{brokercode}/station-maintenances/{maintenanceid}", BROKER_CODE, MAINTENANCE_ID)
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getStationMaintenances() throws Exception {
        StationMaintenanceResource maintenanceResource = new StationMaintenanceResource();
        maintenanceResource.setStationCode(STATION_CODE);
        maintenanceResource.setStandIn(true);
        maintenanceResource.setEndDateTime(OffsetDateTime.now());
        maintenanceResource.setStartDateTime(OffsetDateTime.now());
        maintenanceResource.setMaintenanceId(MAINTENANCE_ID);
        maintenanceResource.setBrokerCode(BROKER_CODE);
        StationMaintenanceListResource response = new StationMaintenanceListResource();
        response.setMaintenanceList(Collections.singletonList(maintenanceResource));
        response.setPageInfo(new PageInfo());
        when(stationMaintenanceService.getStationMaintenances(anyString(), anyString(), any(StationMaintenanceListState.class), anyInt(), anyInt(), anyInt())).thenReturn(response);

        mvc.perform(get("/brokers/{brokercode}/station-maintenances", BROKER_CODE)
                        .param("stationCode", STATION_CODE)
                        .param("state", String.valueOf(StationMaintenanceListState.SCHEDULED_AND_IN_PROGRESS))
                        .param("year", String.valueOf(2024))
                        .param("limit", String.valueOf(0))
                        .param("page", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBrokerMaintenancesSummaryTest() throws Exception {
        when(stationMaintenanceService.getBrokerMaintenancesSummary(anyString(), anyString()))
                .thenReturn(MaintenanceHoursSummaryResource.builder()
                        .usedHours("2")
                        .scheduledHours("3")
                        .remainingHours("31")
                        .extraHours("0")
                        .annualHoursLimit("36")
                        .build());

        mvc.perform(get("/brokers/{brokercode}/station-maintenances/summary", BROKER_CODE)
                        .param("maintenanceYear", "2024")
                ).andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
