package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigSelfcareIntegrationService;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.core.JiraServiceManagerService;
import it.pagopa.selfcare.pagopa.backoffice.core.WrapperService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.WrapperStationDetailsDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private StationMapper mapper = Mappers.getMapper(StationMapper.class);
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    JiraServiceManagerService jiraServiceManagerService;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @MockBean
    private ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService;

    @MockBean
    private WrapperService wrapperServiceMock;

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

        when(apiConfigServiceMock.getStations(anyInt(), anyInt(), any(), any(), any(), any(), any()))
                .thenReturn(stations);
        //when
        mvc.perform(get(BASE_URL)
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
                .getStations(eq(limit), eq(page), eq(sort), isNull(), eq(creditorInstitutionCode), isNull(), anyString());
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
        mvc.perform(get(BASE_URL + "/details/{stationId}", stationId)
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

    @Test
    void createStationTest(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        // Given
        StationDetailsDto stationDetailsDto = objectMapper.readValue(dto.getInputStream(), StationDetailsDto.class);
        StationDetails stationDetails = mapper.fromDto(stationDetailsDto);
        when(apiConfigServiceMock.createStation(any(), anyString())).thenReturn(stationDetails);

        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        wrapperEntity.setEntity(stationDetails);
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setEntities(List.of(wrapperEntity));

        when(wrapperServiceMock.updateWrapperStationDetailsByOpt(any(), anyString(), anyString()))
                .thenReturn(wrapperEntities);

        // When
        mvc.perform(MockMvcRequestBuilders.
                        post(BASE_URL)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.modifiedBy", notNullValue()))
                .andExpect(jsonPath("$.entity", notNullValue()))
                .andExpect(jsonPath("$.entity.ip", notNullValue()))
                .andExpect(jsonPath("$.entity.port", notNullValue()))
                .andExpect(jsonPath("$.entity.protocol", notNullValue()));

        //then
        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetailsByOpt(eq(stationDetails), eq(stationDetailsDto.getNote()), eq(WrapperStatus.APPROVED.name()));
    }

    @Test
    void getStationCode() throws Exception {
        //given
        String ecCode = "ecCode";
        String stationCode = "stationCode";
        WrapperEntitiesList entitiesList = mockInstance(new WrapperEntitiesList());
        entitiesList.setWrapperEntities(new ArrayList<>());

        when(apiConfigServiceMock.generateStationCode(anyString(), any()))
                .thenReturn(stationCode);
        when(wrapperServiceMock.findByStatusAndTypeAndBrokerCodeAndIdLike(any(), any(), any(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(entitiesList);

        //when
        mvc.perform(get(BASE_URL + "/{ecCode}/generate", ecCode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.stationCode", is(stationCode)));
        //then
        verify(apiConfigServiceMock, times(1))
                .generateStationCode(eq(ecCode), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void associateStationToCreditorInstitution() throws Exception {
        //given
        String ecCode = "ecCode";
        CreditorInstitutionStationDto station = mockInstance(new CreditorInstitutionStationDto());
        CreditorInstitutionStationEdit response = mockInstance(new CreditorInstitutionStationEdit());

        when(apiConfigServiceMock.createCreditorInstitutionStationRelation(anyString(), any(), anyString()))
                .thenReturn(response);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/{ecCode}/station", ecCode)
                        .content(objectMapper.writeValueAsBytes(station))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.stationCode", is(station.getStationCode())));
        //then
        ArgumentCaptor<CreditorInstitutionStationEdit> stationArgumentCaptor = ArgumentCaptor.forClass(CreditorInstitutionStationEdit.class);
        verify(apiConfigServiceMock, times(1))
                .createCreditorInstitutionStationRelation(eq(ecCode), stationArgumentCaptor.capture(), anyString());
        CreditorInstitutionStationEdit captured = stationArgumentCaptor.getValue();
        assertNotNull(captured);
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void createWrapperStationDetails(@Value("classpath:stubs/WrapperStationDto.json") Resource dto) throws Exception {
        //given
        String note = "note";
        WrapperStatus status = WrapperStatus.TO_CHECK;

        InputStream is = dto.getInputStream();
        WrapperStationDetailsDto wrapperStationDetailsDto = objectMapper.readValue(is, WrapperStationDetailsDto.class);
        StationDetails stationDetails = mapper.fromWrapperStationDetailsDto(wrapperStationDetailsDto);


        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setEntities(List.of(wrapperEntity));

        when(wrapperServiceMock.createWrapperStationDetails(any(), anyString(), anyString()))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/create-wrapperStation")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.status", is(wrapperEntities.getStatus().name())))
                .andExpect(jsonPath("$.type", is(wrapperEntities.getType().name())))
                .andExpect(jsonPath("$.entities", notNullValue()));
        //then
        verify(wrapperServiceMock, times(1))
                .createWrapperStationDetails(eq(stationDetails), eq(note), eq(status.name()));
        verifyNoMoreInteractions(apiConfigServiceMock);
    }


    @Test
    void updateWrapperStationDetails(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        //given
        StationDetails stationDetails = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));

        InputStream is = dto.getInputStream();

        StationDetailsDto stationDetailsDto = objectMapper.readValue(is, StationDetailsDto.class);
        StationDetails fromStationDetailsDto = mapper.fromDto(stationDetailsDto);
        DummyWrapperEntity<StationDetails> wrapperEntityDto = new DummyWrapperEntity<>(fromStationDetailsDto);
        wrapperEntities.getEntities().add(wrapperEntityDto);
        String status = stationDetailsDto.getStatus().name();
        String note = stationDetailsDto.getNote();
        when(wrapperServiceMock.updateWrapperStationDetails(fromStationDetailsDto, note, status, null))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/update-wrapperStation")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.type", is(wrapperEntities.getType().name())))
                .andExpect(jsonPath("$.entities", notNullValue()));
        //then
        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetails(any(), anyString(), anyString(), eq(null));

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void updateWrapperStationDetailsByOpt(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        //given
        String stationCode = "stationCode";
        StationDetails stationDetails = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));

        InputStream is = dto.getInputStream();

        StationDetailsDto stationDetailsDto = objectMapper.readValue(is, StationDetailsDto.class);
        StationDetails fromStationDetailsDto = mapper.fromDto(stationDetailsDto);
        DummyWrapperEntity<StationDetails> wrapperEntityDto = new DummyWrapperEntity<>(fromStationDetailsDto);
        wrapperEntities.getEntities().add(wrapperEntityDto);
        String status = stationDetailsDto.getStatus().name();
        String note = stationDetailsDto.getNote();
        when(wrapperServiceMock.updateWrapperStationDetailsByOpt(fromStationDetailsDto, note, status))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/update-wrapperStationByOpt")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.type", is(wrapperEntities.getType().name())))
                .andExpect(jsonPath("$.entities", notNullValue()));
        //then
        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetailsByOpt(any(), anyString(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void updateStation(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";
        String stationCode = "string";
        InputStream is = dto.getInputStream();
        StationDetailsDto stationDetailsDto = objectMapper.readValue(is, StationDetailsDto.class);
        StationDetails stationDetails = mapper.fromDto(stationDetailsDto);
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        DummyWrapperEntity<StationDetails> wrapperEntityDto = new DummyWrapperEntity<>(stationDetails);
        wrapperEntities.getEntities().add(wrapperEntityDto);

        when(apiConfigServiceMock.updateStation(anyString(), any(), anyString()))
                .thenReturn(stationDetails);
        when(wrapperServiceMock.updateWrapperStationDetails(any(), anyString(), anyString(), anyString()))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/{stationcode}", stationCode)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-Request-Id", String.valueOf(xRequestId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.password", is(stationDetails.getPassword())))
                .andExpect(jsonPath("$.newPassword", notNullValue()))
                .andExpect(jsonPath("$.protocol", is(stationDetails.getProtocol().name())))
                .andExpect(jsonPath("$.ip", is(stationDetails.getIp())))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.service", is(stationDetails.getService())))
                .andExpect(jsonPath("$.proxyEnabled", is(stationDetails.getProxyEnabled())))
                .andExpect(jsonPath("$.proxyHost", is(stationDetails.getProxyHost())))
                .andExpect(jsonPath("$.proxyPort", notNullValue()))
                .andExpect(jsonPath("$.proxyUsername", is(stationDetails.getProxyUsername())))
                .andExpect(jsonPath("$.targetHost", is(stationDetails.getTargetHost())))
                .andExpect(jsonPath("$.targetPort", notNullValue()))
                .andExpect(jsonPath("$.targetPath", is(stationDetails.getTargetPath())))
                .andExpect(jsonPath("$.threadNumber", notNullValue()))
                .andExpect(jsonPath("$.timeoutA", notNullValue()))
                .andExpect(jsonPath("$.timeoutB", notNullValue()))
                .andExpect(jsonPath("$.timeoutC", notNullValue()))
                .andExpect(jsonPath("$.redirectIp", is(stationDetails.getRedirectIp())))
                .andExpect(jsonPath("$.redirectPath", is(stationDetails.getRedirectPath())));

        //then
        verify(apiConfigServiceMock, times(1))
                .updateStation(anyString(), any(), anyString());

        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetails(any(), anyString(), anyString(), eq(null));

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getWrapperEntities_station() throws Exception {
        //given
        String code = "code";
        StationDetails stationDetails = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setEntities(List.of(wrapperEntity));

        when(wrapperServiceMock.findById(anyString()))
                .thenReturn(wrapperEntities);
        //when
        mvc.perform(get(BASE_URL + "/get-wrapperEntities/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status", is(wrapperEntities.getStatus().name())))
                .andExpect(jsonPath("$.type", is(wrapperEntities.getType().name())))
                .andExpect(jsonPath("$.entities", notNullValue()));

        verify(wrapperServiceMock, times(1))
                .findById(anyString());

        verifyNoMoreInteractions(wrapperServiceMock);
    }

    @Test
    void getAllStationsMerged() throws Exception {
        //given
        WrapperType wrapperType = WrapperType.STATION;
        String stationCode = "stationCode";
        String brokerCode = "brokerCode";
        Integer page = 0;
        Integer size = 50;
        String sorting = "ASC";

        Stations stations = mockInstance(new Stations());
        List<Station> stationList = mockInstance(new ArrayList<>());
        stations.setStationsList(stationList);

        StationDetails stationDetails = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        wrapperEntity.setEntity(stationDetails);
        wrapperEntity.setModifiedAt(Instant.now());
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setEntities(List.of(wrapperEntity));

        WrapperEntitiesList mongoList = mockInstance(new WrapperEntitiesList());
        PageInfo pageInfo = mockInstance(new PageInfo());
        mongoList.setWrapperEntities(List.of(wrapperEntities));
        mongoList.setPageInfo(pageInfo);
        WrapperStations wrapperStations1 = mockInstance(new WrapperStations());

        List<WrapperStation> w1List = new ArrayList<>();
        WrapperStation w1 = new WrapperStation();
        w1List.add(w1);
        wrapperStations1.setStationsList(w1List);



        when(wrapperServiceMock.findByIdLikeOrTypeOrBrokerCode(stationCode, wrapperType, brokerCode, page, size))
                .thenReturn(mongoList);
        when(apiConfigServiceMock.getStations(anyInt(), anyInt(), anyString(), anyString(), isNull(), anyString(), anyString()))
                .thenReturn(stations);
        when(apiConfigServiceMock.mergeAndSortWrapperStations(any(), any(), anyString()))
                .thenReturn(wrapperStations1);

        //when
        mvc.perform(get(BASE_URL + "/getAllStations")

                        .queryParam("limit", String.valueOf(size))
                        .queryParam("stationcodefilter", stationCode)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("sorting", sorting)
                        .queryParam("brokerCode", brokerCode)
                
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.stationsList", hasSize(1)));

        //then
        verify(wrapperServiceMock, times(1))
                .findByIdLikeOrTypeOrBrokerCode(anyString(), any(), anyString(), anyInt(), anyInt());
        verify(apiConfigServiceMock, times(1))
                .getStations(anyInt(), anyInt(), anyString(), anyString(), isNull(), anyString(), anyString());
        verify(apiConfigServiceMock, times(1))
                .mergeAndSortWrapperStations(any(), any(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getStationsDetailsListByBroker() throws Exception {
        //given
        String stationId = "code";
        String brokerId = "stationCode";
        Integer page = 0;
        Integer limit = 50;
        StationDetailsList stationDetailsList = mockInstance(new StationDetailsList());
        StationDetails stationDetails = mockInstance(new StationDetails());
        stationDetailsList.setStationsDetailsList(List.of(stationDetails));


        when(apiConfigSelfcareIntegrationService.getStationsDetailsListByBroker(anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(stationDetailsList);
        //when
        mvc.perform(get(BASE_URL + "/{brokerId}", brokerId)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("stationId", stationId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.stations[*].stationCode", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].ip", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].newPassword", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].password", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].port", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].protocol", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].redirectIp", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].redirectPath", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].redirectPort", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].redirectQueryString", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].redirectProtocol", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].service", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].pofService", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].brokerCode", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].protocol4Mod", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].ip4Mod", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].port4Mod", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].service4Mod", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].proxyEnabled", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].proxyHost", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].proxyPort", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].proxyUsername", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].proxyPassword", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].threadNumber", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].timeoutA", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].timeoutB", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].timeoutC", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].flagOnline", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].brokerObjId", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].rtInstantaneousDispatch", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].targetHost", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].targetPort", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].targetPath", everyItem(notNullValue())))
                .andExpect(jsonPath("$.stations[*].primitiveVersion", everyItem(notNullValue())));

        verify(apiConfigSelfcareIntegrationService, times(1))
                .getStationsDetailsListByBroker(anyString(), anyString(), anyInt(), anyInt(), anyString());

        verifyNoMoreInteractions(wrapperServiceMock);

    }
    @Test
     void getStationDetail_mongo() throws Exception {
        //given
        String stationId = "stationId";
        StationDetails station = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(station));
        wrapperEntity.setEntity(station);
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setEntities(List.of(wrapperEntity));

        when(wrapperServiceMock.findById(stationId))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(get(BASE_URL + "/get-details/{stationId}", stationId)
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
        verify(wrapperServiceMock, times(1))
                .findById(anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getStationDetail_apiConfig() throws Exception {
        //given
        String stationId = "stationId";
        StationDetails station = mockInstance(new StationDetails());

        doThrow(ResourceNotFoundException.class).when(wrapperServiceMock).findById(stationId);

        when(apiConfigServiceMock.getStation(anyString(), anyString()))
                .thenReturn(station);
        //when
        mvc.perform(get(BASE_URL + "/get-details/{stationId}", stationId)
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

    @Test
    void createBroker(@Value("classpath:stubs/brokerDto.json") Resource dto) throws Exception {
        // Given
        BrokerDto brokerDto = objectMapper.readValue(dto.getInputStream(), BrokerDto.class);
        BrokerDetails broker = BrokerMapper.fromDto(brokerDto);
        when(apiConfigServiceMock.createBroker(any(), anyString())).thenReturn(broker);

        // When
        mvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/create-broker")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.broker_code", notNullValue()))
                .andExpect(jsonPath("$.extended_fault_bean", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.enabled", notNullValue()));

        //then
        verify(apiConfigServiceMock, times(1))
                .createBroker(any(), anyString());
    }

    @Test
    void getCreditorInstitutionsByStationCode() throws Exception {
        //given
        String stationCode = "stationCode";
        Integer page = 0;

        CreditorInstitution creditorInstitution = mockInstance(new CreditorInstitution());
        CreditorInstitutions creditorInstitutions = mockInstance(new CreditorInstitutions());
        List<CreditorInstitution> creditorInstitutionList = new ArrayList<>();
        creditorInstitutionList.add(creditorInstitution);
        creditorInstitutions.setCreditorInstitutionList(creditorInstitutionList);


        when(apiConfigServiceMock.getCreditorInstitutionsByStation(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(creditorInstitutions);

        //when
        mvc.perform(get(BASE_URL + "/getCreditorInstitutions/{stationcode}", stationCode)
                        .queryParam("page", String.valueOf(page))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.creditor_institutions", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getCreditorInstitutionsByStation(anyString(), anyInt(), anyInt(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void deleteCreditorInstitutionStationRelationship() throws Exception{

        //given
        String ecCode = "ecCode";
        String xRequestId = "1";
        String stationcode = "stationcode";

        doNothing().when(apiConfigServiceMock).deleteCreditorInstitutionStationRelationship(anyString(), anyString(), anyString());

        //when
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/{ecCode}/station/{stationcode}", ecCode, stationcode)
                        .header("X-Request-Id", xRequestId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        //then
        verify(apiConfigServiceMock, times(1))
                .deleteCreditorInstitutionStationRelationship(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getStationCodeV2() throws Exception {
        //given
        WrapperType wrapperType = WrapperType.STATION;
        String stationCode = "stationCode";
        String brokerCode = null;
        Integer page = 0;
        Integer size = 100;
        String sorting = "ASC";

        Stations stations = mockInstance(new Stations());
        List<Station> stationList = mockInstance(new ArrayList<>());
        stations.setStationsList(stationList);

        StationDetails stationDetails = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        wrapperEntity.setEntity(stationDetails);
        wrapperEntity.setModifiedAt(Instant.now());
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setModifiedAt(Instant.now());
        wrapperEntities.setEntities(List.of(wrapperEntity));

        WrapperEntitiesList mongoList = mockInstance(new WrapperEntitiesList());
        PageInfo pageInfo = mockInstance(new PageInfo());
        mongoList.setWrapperEntities(List.of(wrapperEntities));
        mongoList.setPageInfo(pageInfo);
        WrapperStations wrapperStations1 = mockInstance(new WrapperStations());

        List<WrapperStation> w1List = new ArrayList<>();
        WrapperStation w1 = new WrapperStation();
        w1List.add(w1);
        wrapperStations1.setStationsList(w1List);




        when(wrapperServiceMock.findByIdLikeOrTypeOrBrokerCode(stationCode, WrapperType.STATION, null, 0, 100))
                .thenReturn(mongoList);
        when(apiConfigServiceMock.getStations(eq(100), eq(0), eq("ASC"), eq(null), eq(null), eq(stationCode), any()))
                .thenReturn(stations);
        when(apiConfigServiceMock.mergeAndSortWrapperStations(any(), any(), anyString()))
                .thenReturn(wrapperStations1);
        when(apiConfigServiceMock.generateStationCodeV2(any(), anyString(), anyString()))
                .thenReturn("stationCode_01");

        //when
        mvc.perform(get(BASE_URL + "/{ecCode}/generateV2", stationCode)

                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());

        //then
        verify(wrapperServiceMock, times(1))
                .findByIdLikeOrTypeOrBrokerCode(stationCode, WrapperType.STATION, null, 0, 100);
        verify(apiConfigServiceMock, times(1))
                .getStations(eq(100), eq(0), eq("ASC"), eq(null), eq(null), eq(stationCode), any());
        verify(apiConfigServiceMock, times(1))
                .mergeAndSortWrapperStations(any(), any(), anyString());
        verify(apiConfigServiceMock, times(1))
                .generateStationCodeV2(any(), anyString(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getBrokersEC() throws Exception {
        //given
        Integer page = 0;
        Integer limit = 50;
        String code = "code";
        String name = "name";
        String orderby = "NAME";
        String ordering = "DESC";
        Brokers brokers = mockInstance(new Brokers());
        Broker broker = mockInstance(new Broker());
        brokers.setBrokerList(new ArrayList<>());
        brokers.getBrokerList().add(broker);

        when(apiConfigServiceMock.getBrokersEC( anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(brokers);

        //when
        mvc.perform(get(BASE_URL + "/brokers-EC")
                        .queryParam("limit", String.valueOf(page))
                        .queryParam("page", String.valueOf(limit))
                        .queryParam("code", code)
                        .queryParam("name", name)
                        .queryParam("orderby", orderby)
                        .queryParam("ordering", ordering)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful());
        //then
        verify(apiConfigServiceMock, times(1))
                .getBrokersEC( anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }
}



