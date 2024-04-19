package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestResultEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import it.pagopa.selfcare.pagopa.backoffice.service.StationService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class StationControllerTest {

    @MockBean
    private StationService stationService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(stationService);
    }

    @Inject
    ObjectMapper objectMapper;

    @Test
    void testStationShouldReturnSuccess() throws Exception {
        StationTestDto stationTestDto = StationTestDto.builder().hostUrl("hostUrk").hostPort(80).hostPath("test").build();
        when(stationService.testStation(stationTestDto))
                .thenReturn(TestStationResource.builder().testResult(TestResultEnum.SUCCESS).build());
        String content = mvc.perform(post("/stations/connection/test")
                        .content(objectMapper.writeValueAsBytes(stationTestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertNotNull(content);
        TestStationResource testStationResource = objectMapper.readValue(content, TestStationResource.class);
        assertEquals(TestResultEnum.SUCCESS, testStationResource.getTestResult());
        verify(stationService).testStation(any());
    }

    @Test
    void testStationShouldReturnErrorResponse() throws Exception {
        StationTestDto stationTestDto = StationTestDto.builder().hostUrl("hostUrk").hostPort(80).hostPath("test").build();
        when(stationService.testStation(stationTestDto))
                .thenReturn(TestStationResource.builder().testResult(TestResultEnum.ERROR).build());
        String content = mvc.perform(post("/stations/connection/test")
                        .content(objectMapper.writeValueAsBytes(stationTestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertNotNull(content);
        TestStationResource testStationResource = objectMapper.readValue(content, TestStationResource.class);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(stationService).testStation(any());
    }

    @Test
    void testStationShouldReturnBadRequestOnMissingContent() throws Exception {
        StationTestDto stationTestDto = StationTestDto.builder().hostUrl("hostUrk").hostPort(80).hostPath("test").build();
        when(stationService.testStation(stationTestDto))
                .thenReturn(TestStationResource.builder().testResult(TestResultEnum.ERROR).build());
        mvc.perform(post("/stations/connection/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testStationShouldReturnBadRequestOnMissingMandatoryField() throws Exception {
        StationTestDto stationTestDto = StationTestDto.builder().hostPort(80).hostPath("test").build();
        when(stationService.testStation(stationTestDto))
                .thenReturn(TestStationResource.builder().testResult(TestResultEnum.ERROR).build());
        mvc.perform(post("/stations/connection/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
