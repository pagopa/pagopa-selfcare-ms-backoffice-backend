package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {StationController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        StationController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class StationControllerTest {
    private static final String BASE_URL = "/stations";
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @Test
    void getStations() throws Exception {
        //given
        Integer limit = 1;
        Integer page = 1;
        String sort = "DESC";
        String creditorInstitutionCode = "creditorInstitutionCode";
        Stations stations = mockInstance(new Stations());
        Station station = mockInstance(new Station());
        PageInfo pageInfo = mockInstance(new PageInfo());
        stations.setStationsList(List.of(station));
        stations.setPageInfo(pageInfo);

        when(apiConfigServiceMock.getStations(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(stations);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .queryParam("sort", sort)
                        .queryParam("creditorInstitutionCode", creditorInstitutionCode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo", notNullValue()))
                .andExpect(jsonPath("$.stationsList", notNullValue()))
                .andExpect(jsonPath("$.stationsList", not(empty())))
                .andExpect(jsonPath("$.stationsList[0].stationCode", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getStations(eq(limit), eq(page), eq(sort), eq(creditorInstitutionCode), isNull(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getStation() throws Exception {
        //given
        String stationId = "stationId";
        StationDetails station = mockInstance(new StationDetails());

        when(apiConfigServiceMock.getStation(anyString(), anyString()))
                .thenReturn(station);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/details/{stationId}", stationId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationCode", notNullValue()))
                .andExpect(jsonPath("$.ip", notNullValue()))
                .andExpect(jsonPath("$.newPassword", notNullValue()))
                .andExpect(jsonPath("$.password", notNullValue()))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.protocol", notNullValue()))
                .andExpect(jsonPath("$.redirectIp", notNullValue()))
                .andExpect(jsonPath("$.redirectPath", notNullValue()))
                .andExpect(jsonPath("$.redirectPort", notNullValue()))
                .andExpect(jsonPath("$.redirectQueryString", notNullValue()))
                .andExpect(jsonPath("$.redirectProtocol", notNullValue()))
                .andExpect(jsonPath("$.service", notNullValue()))
                .andExpect(jsonPath("$.pofService", notNullValue()))
                .andExpect(jsonPath("$.brokerCode", notNullValue()))
                .andExpect(jsonPath("$.protocol4Mod", notNullValue()))
                .andExpect(jsonPath("$.ip4Mod", notNullValue()))
                .andExpect(jsonPath("$.port4Mod", notNullValue()))
                .andExpect(jsonPath("$.service4Mod", notNullValue()))
                .andExpect(jsonPath("$.proxyEnabled", notNullValue()))
                .andExpect(jsonPath("$.proxyHost", notNullValue()))
                .andExpect(jsonPath("$.proxyPort", notNullValue()))
                .andExpect(jsonPath("$.proxyUsername", notNullValue()))
                .andExpect(jsonPath("$.proxyPassword", notNullValue()))
                .andExpect(jsonPath("$.threadNumber", notNullValue()))
                .andExpect(jsonPath("$.timeoutA", notNullValue()))
                .andExpect(jsonPath("$.timeoutB", notNullValue()))
                .andExpect(jsonPath("$.timeoutC", notNullValue()))
                .andExpect(jsonPath("$.flagOnline", notNullValue()))
                .andExpect(jsonPath("$.brokerObjId", notNullValue()))
                .andExpect(jsonPath("$.rtInstantaneousDispatch", notNullValue()))
                .andExpect(jsonPath("$.targetHost", notNullValue()))
                .andExpect(jsonPath("$.targetPort", notNullValue()))
                .andExpect(jsonPath("$.targetPath", notNullValue()))
                .andExpect(jsonPath("$.primitiveVersion", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getStation(eq(stationId), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

}