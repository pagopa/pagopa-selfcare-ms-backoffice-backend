package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestResultEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationTypeEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.WrapperStationsResource;
import it.pagopa.selfcare.pagopa.backoffice.service.StationService;
import org.jetbrains.annotations.NotNull;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void createStation() throws Exception {
        when(stationService.createStation(any())).thenReturn(new WrapperEntity<>());
        mvc.perform(post("/stations")
                        .content(objectMapper.writeValueAsBytes(new StationDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getStations() throws Exception {
        when(stationService.getStations(any(), eq(null), anyString(), eq(null), anyInt()))
                .thenReturn(new WrapperStationsResource());
        mvc.perform(get("/stations")
                        .param("status", "ACTIVE")
                        .param("brokerCode", "brokerCode")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getStation() throws Exception {
        when(stationService.getStation(anyString())).thenReturn(buildStationDetailResource());
        mvc.perform(get("/stations/{station-code}", "stationCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getCreditorInstitutionsByStationCode() throws Exception {
        when(stationService.getCreditorInstitutionsByStationCode(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(new CreditorInstitutionsResource());
        mvc.perform(get("/stations/{station-code}/creditor-institutions", "stationCode")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateStation() throws Exception {

        when(stationService.updateStation(any(), anyString())).thenReturn(buildStationDetailResource());
        mvc.perform(put("/stations/{station-code}", "stationCode")
                        .content(objectMapper.writeValueAsBytes(buildStationDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getWrapperEntitiesStation() throws Exception {
        when(stationService.getWrapperEntitiesStation(anyString())).thenReturn(new WrapperEntities());
        mvc.perform(get("/stations/wrapper/{station-code}", "stationCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getAllStationsMerged() throws Exception {
        when(stationService.getAllStationsMerged(anyInt(), eq(null), anyString(), anyInt(), eq(null)))
                .thenReturn(buildWrapperStationsResource());
        mvc.perform(get("/stations/merged")
                        .param("brokerCode", "brokerCode")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void createWrapperStationDetails() throws Exception {
        when(stationService.createWrapperStationDetails(any())).thenReturn(new WrapperEntities<>());
        mvc.perform(post("/stations/wrapper")
                        .content(objectMapper.writeValueAsBytes(buildWrapperStationDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getStationCode() throws Exception {
        when(stationService.getStationCode(anyString(), eq(false))).thenReturn(new StationCodeResource("stationCode"));
        mvc.perform(get("/stations/station-code")
                        .param("ec-code", "ec-code")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getStationCodeV2() throws Exception {
        when(stationService.getStationCode(anyString(), eq(true))).thenReturn(new StationCodeResource("stationCode"));
        mvc.perform(get("/stations/station-code/v2")
                        .param("ec-code", "ec-code")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateWrapperStationDetails() throws Exception {
        StationDetailsDto dto = buildStationDetailsDto();

        when(stationService.updateWrapperStationDetails(any())).thenReturn(new WrapperEntities());
        mvc.perform(put("/stations/wrapper/{station-code}", "stationCode")
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateWrapperStationDetailsByOpt() throws Exception {
        when(stationService.updateWrapperStationDetailsByOpt(any())).thenReturn(new WrapperEntities());
        mvc.perform(put("/stations/wrapper/operator")
                        .content(objectMapper.writeValueAsBytes(buildStationDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testStationShouldReturnSuccess() throws Exception {
        StationTestDto stationTestDto = StationTestDto.builder()
                .hostUrl("hostUrk").hostPort(80)
                .hostPath("test")
                .testStationType(TestStationTypeEnum.PA_REDIRECT)
                .build();
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
        StationTestDto stationTestDto = StationTestDto.builder().hostUrl("hostUrk").hostPort(80)
                .hostPath("test").testStationType(TestStationTypeEnum.PA_VERIFY).build();
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


    private @NotNull StationDetailsDto buildStationDetailsDto() {
        StationDetailsDto dto = new StationDetailsDto();
        dto.setStationCode("stationCode");
        dto.setVersion(1L);
        dto.setBrokerCode("brokerCode");
        dto.setPrimitiveVersion(1);
        return dto;
    }

    private @NotNull StationDetailResource buildStationDetailResource() {
        StationDetailResource resource = new StationDetailResource();
        resource.setVersion(1L);
        resource.setStationCode("stationCode");
        resource.setAssociatedCreditorInstitutions(1);
        return resource;
    }

    private @NotNull WrapperStationsResource buildWrapperStationsResource() {
        WrapperStationResource resource = new WrapperStationResource();
        resource.setStationCode("stationCode");
        resource.setVersion(1L);
        resource.setAssociatedCreditorInstitutions(1);
        resource.setWrapperStatus(WrapperStatus.APPROVED);
        WrapperStationsResource stationsResource = new WrapperStationsResource();
        stationsResource.setStationsList(Collections.singletonList(resource));
        return stationsResource;
    }

    private @NotNull WrapperStationDetailsDto buildWrapperStationDetailsDto() {
        WrapperStationDetailsDto dto = new WrapperStationDetailsDto();
        dto.setPrimitiveVersion(1);
        dto.setStationCode("stationCode");
        return dto;
    }
}
