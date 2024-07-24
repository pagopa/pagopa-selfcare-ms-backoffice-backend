package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void getStationMaintenanceTest() throws Exception {
        when(stationMaintenanceService.getStationMaintenance(anyString(), anyLong()))
                .thenReturn(buildMaintenanceResource());

        mvc.perform(get("/brokers/{brokercode}/station-maintenances/{maintenanceid}", BROKER_CODE, MAINTENANCE_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getStationMaintenanceTestOnKO() throws Exception {
        when(stationMaintenanceService.getStationMaintenance(anyString(), anyLong()))
                .thenThrow(new AppException(AppError.INTERNAL_SERVER_ERROR));

        mvc.perform(get("/brokers/{brokercode}/station-maintenances/{maintenanceid}", BROKER_CODE, MAINTENANCE_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
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
