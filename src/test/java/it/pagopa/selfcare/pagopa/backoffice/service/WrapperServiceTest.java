package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannel;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannels;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStation;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperChannelList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.WrapperStationList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperChannelsRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperStationsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = WrapperService.class)
class WrapperServiceTest {

    private static final String STATION_CODE = "stationCode";
    private static final String CHANNEL_CODE = "channelCode";
    private static final String BROKER_CODE = "brokerCode";
    private static final int PAGE = 0;
    private static final int LIMIT = 10;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private WrapperRepository repository;

    @MockBean
    private WrapperStationsRepository wrapperStationsRepository;

    @MockBean
    private WrapperChannelsRepository wrapperChannelsRepository;

    @MockBean
    private AuditorAware<String> auditorAware;

    @Captor
    private ArgumentCaptor<WrapperEntityChannels> argumentCaptorChannels;

    @Captor
    private ArgumentCaptor<WrapperEntityStations> argumentCaptorStations;

    @Autowired
    private WrapperService sut;

    @Test
    void getFirstValidCodeV2SuccessPT() {
        Channels channels = Channels.builder()
                .channelList(Collections.singletonList(Channel.builder()
                        .channelCode(BROKER_CODE + "_01")
                        .enabled(true)
                        .build()))
                .build();
        Stations stations = Stations.builder()
                .stationsList(Collections.singletonList(Station.builder()
                        .stationCode(BROKER_CODE + "_02")
                        .enabled(true)
                        .version(1L)
                        .build()))
                .build();
        WrapperEntityChannels wrapperEntityChannels = new WrapperEntityChannels();
        wrapperEntityChannels.setId(BROKER_CODE + "_03");
        WrapperEntityStations wrapperEntityStations = new WrapperEntityStations();
        wrapperEntityStations.setId(BROKER_CODE + "_04");

        when(apiConfigClient.getChannels(null, BROKER_CODE, Sort.Direction.DESC.name(), 100, 0))
                .thenReturn(channels);
        when(apiConfigClient.getStations(100, 0, Sort.Direction.DESC.name(), BROKER_CODE, null, null))
                .thenReturn(stations);
        when(wrapperChannelsRepository.findByTypeAndBrokerCode(any(), any(), any())).thenReturn(
                new PageImpl<>(Collections.singletonList(wrapperEntityChannels)));
        when(wrapperStationsRepository.findByTypeAndBrokerCode(any(), any(), any())).thenReturn(
                new PageImpl<>(Collections.singletonList(wrapperEntityStations)));

        String result = assertDoesNotThrow(() -> sut.getFirstValidCodeV2(BROKER_CODE));

        assertNotNull(result);
        assertEquals(BROKER_CODE + "_05", result);
    }

    @Test
    void getFirstValidCodeV2SuccessEC() {
        Channels channels = Channels.builder()
                .channelList(Collections.emptyList())
                .build();
        Stations stations = Stations.builder()
                .stationsList(Collections.singletonList(Station.builder()
                        .stationCode(BROKER_CODE + "_02")
                        .enabled(true)
                        .version(1L)
                        .build()))
                .build();
        WrapperEntityStations wrapperEntityStations = new WrapperEntityStations();
        wrapperEntityStations.setId(BROKER_CODE + "_03");

        when(apiConfigClient.getChannels(null, BROKER_CODE, Sort.Direction.DESC.name(), 100, 0))
                .thenReturn(channels);
        when(apiConfigClient.getStations(100, 0, Sort.Direction.DESC.name(), BROKER_CODE, null, null))
                .thenReturn(stations);
        when(wrapperChannelsRepository.findByTypeAndBrokerCode(any(), any(), any())).thenReturn(
                new PageImpl<>(Collections.emptyList()));
        when(wrapperStationsRepository.findByTypeAndBrokerCode(any(), any(), any())).thenReturn(
                new PageImpl<>(Collections.singletonList(wrapperEntityStations)));

        String result = assertDoesNotThrow(() -> sut.getFirstValidCodeV2(BROKER_CODE));

        assertNotNull(result);
        assertEquals(BROKER_CODE + "_01", result);
    }

    @Test
    void getFirstValidCodeV2SuccessPSP() {
        Channels channels = Channels.builder()
                .channelList(Collections.singletonList(Channel.builder()
                        .channelCode(BROKER_CODE + "_01")
                        .enabled(true)
                        .build()))
                .build();
        WrapperEntityChannels wrapperEntityChannels = new WrapperEntityChannels();
        wrapperEntityChannels.setId(BROKER_CODE + "_03");

        when(apiConfigClient.getChannels(null, BROKER_CODE, Sort.Direction.DESC.name(), 100, 0))
                .thenReturn(channels);
        when(apiConfigClient.getStations(100, 0, Sort.Direction.DESC.name(), BROKER_CODE, null, null))
                .thenThrow(FeignException.NotFound.class);
        when(wrapperChannelsRepository.findByTypeAndBrokerCode(any(), any(), any())).thenReturn(
                new PageImpl<>(Collections.singletonList(wrapperEntityChannels)));

        String result = assertDoesNotThrow(() -> sut.getFirstValidCodeV2(BROKER_CODE));

        assertNotNull(result);
        assertEquals(BROKER_CODE + "_02", result);

        verify(wrapperStationsRepository, never()).findByTypeAndBrokerCode(any(), any(), any());
    }

    @Test
    void getWrapperStationsWithStationCodeSuccess() {
        when(wrapperStationsRepository.findByIdLikeAndTypeAndBrokerCodeAndStatusNot(
                eq(STATION_CODE),
                eq(WrapperType.STATION),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any())
        ).thenReturn(new PageImpl<>(Collections.singletonList(buildWrapperEntityStations(WrapperStatus.TO_CHECK))));

        WrapperStationList result = assertDoesNotThrow(() ->
                sut.getWrapperStations(STATION_CODE, BROKER_CODE, LIMIT, PAGE));

        assertNotNull(result);
        assertNotNull(result.getPageInfo());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(1, result.getPageInfo().getTotalItems());
        assertEquals(1, result.getPageInfo().getTotalPages());
        assertEquals(1, result.getPageInfo().getItemsFound());

        verify(repository, never()).findByTypeAndBrokerCodeAndStatusNot(
                eq(WrapperType.STATION),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any());
    }

    @Test
    void getWrapperStationsWithoutStationCodeSuccess() {
        when(wrapperStationsRepository.findByTypeAndBrokerCodeAndStatusNot(
                eq(WrapperType.STATION),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any())
        ).thenReturn(new PageImpl<>(Collections.singletonList(buildWrapperEntityStations(WrapperStatus.TO_CHECK))));

        WrapperStationList result = assertDoesNotThrow(() ->
                sut.getWrapperStations(null, BROKER_CODE, LIMIT, PAGE));

        assertNotNull(result);
        assertNotNull(result.getPageInfo());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(1, result.getPageInfo().getTotalItems());
        assertEquals(1, result.getPageInfo().getTotalPages());
        assertEquals(1, result.getPageInfo().getItemsFound());

        verify(repository, never()).findByIdLikeAndTypeAndBrokerCodeAndStatusNot(
                eq(STATION_CODE),
                eq(WrapperType.STATION),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any());
    }

    @Test
    void getWrapperChannelsWithStationCodeSuccess() {
        when(wrapperChannelsRepository.findByIdLikeAndTypeAndBrokerCodeAndStatusNot(
                eq(CHANNEL_CODE),
                eq(WrapperType.CHANNEL),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any())
        ).thenReturn(new PageImpl<>(Collections.singletonList(buildWrapperEntityChannels(WrapperStatus.TO_CHECK))));

        WrapperChannelList result = assertDoesNotThrow(() ->
                sut.getWrapperChannels(CHANNEL_CODE, BROKER_CODE, LIMIT, PAGE));

        assertNotNull(result);
        assertNotNull(result.getPageInfo());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(1, result.getPageInfo().getTotalItems());
        assertEquals(1, result.getPageInfo().getTotalPages());
        assertEquals(1, result.getPageInfo().getItemsFound());

        verify(repository, never()).findByTypeAndBrokerCodeAndStatusNot(
                eq(WrapperType.CHANNEL),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any());
    }

    @Test
    void getWrapperChannelsWithoutStationCodeSuccess() {
        when(wrapperChannelsRepository.findByTypeAndBrokerCodeAndStatusNot(
                eq(WrapperType.CHANNEL),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any())
        ).thenReturn(new PageImpl<>(Collections.singletonList(buildWrapperEntityChannels(WrapperStatus.TO_CHECK))));

        WrapperChannelList result = assertDoesNotThrow(() ->
                sut.getWrapperChannels(null, BROKER_CODE, LIMIT, PAGE));

        assertNotNull(result);
        assertNotNull(result.getPageInfo());
        assertEquals(PAGE, result.getPageInfo().getPage());
        assertEquals(LIMIT, result.getPageInfo().getLimit());
        assertEquals(1, result.getPageInfo().getTotalItems());
        assertEquals(1, result.getPageInfo().getTotalPages());
        assertEquals(1, result.getPageInfo().getItemsFound());

        verify(repository, never()).findByIdLikeAndTypeAndBrokerCodeAndStatusNot(
                eq(CHANNEL_CODE),
                eq(WrapperType.STATION),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any());
    }

    @Test
    void updateStationWithOperatorReviewSuccess() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_CHECK)));

        assertDoesNotThrow(() -> sut.updateStationWithOperatorReview(STATION_CODE, "operator review note"));

        verify(auditorAware).getCurrentAuditor();
        verify(wrapperStationsRepository).save(any());
    }

    @Test
    void updateStationWithOperatorReviewSuccessFailNotFound() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class,
                () -> sut.updateStationWithOperatorReview(STATION_CODE, "operator review note"));

        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());

        verify(auditorAware, never()).getCurrentAuditor();
        verify(wrapperStationsRepository, never()).save(any());
    }

    @Test
    void createWrapperChannelSuccessWithInsert() {
        when(repository.insert(any(WrapperEntities.class))).thenReturn(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK));

        WrapperEntities<ChannelDetails> result = assertDoesNotThrow(() ->
                sut.createWrapperChannel(buildChannelDetails(), WrapperStatus.APPROVED));

        assertNotNull(result);

        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    void createWrapperChannelSuccessWithUpdate() {
        when(repository.insert(any(WrapperEntities.class))).thenThrow(DuplicateKeyException.class);
        when(repository.findById(anyString())).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK)));
        when(repository.save(any())).thenReturn(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK));

        WrapperEntities<ChannelDetails> result = assertDoesNotThrow(() ->
                sut.createWrapperChannel(buildChannelDetails(), WrapperStatus.APPROVED));

        assertNotNull(result);
    }

    @Test
    void createWrapperStationSuccessWithInsert() {
        when(wrapperStationsRepository.insert(any(WrapperEntityStations.class))).thenReturn(buildWrapperEntityStations(WrapperStatus.TO_CHECK));

        WrapperEntityStations result = assertDoesNotThrow(() ->
                sut.createWrapperStation(buildStationDetails(), WrapperStatus.APPROVED));

        assertNotNull(result);

        verify(wrapperStationsRepository, never()).findById(anyString());
        verify(wrapperStationsRepository, never()).save(any());
    }

    @Test
    void createWrapperStationSuccessWithUpdate() {
        when(wrapperStationsRepository.insert(any(WrapperEntityStations.class))).thenThrow(DuplicateKeyException.class);
        when(wrapperStationsRepository.findById(anyString())).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_CHECK)));
        when(wrapperStationsRepository.save(any())).thenReturn(buildWrapperEntityStations(WrapperStatus.TO_CHECK));

        WrapperEntityStations result = assertDoesNotThrow(() ->
                sut.createWrapperStation(buildStationDetails(), WrapperStatus.APPROVED));

        assertNotNull(result);
    }

    @Test
    void updateWrapperChannelSuccessToCheck() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_CHECK)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessToCheckUpdate() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_CHECK_UPDATE)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessToFix() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_FIX)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessToFixUpdate() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_FIX_UPDATE)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessApproved() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.APPROVED)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessNotFoundCreateNewWrapper() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperStationSuccessToCheck() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_CHECK)));

        assertDoesNotThrow(() -> sut.updateWrapperStation(STATION_CODE, buildStationDetails()));

        verify(wrapperStationsRepository).save(argumentCaptorStations.capture());
        assertEquals(WrapperStatus.TO_CHECK, argumentCaptorStations.getValue().getStatus());
    }

    @Test
    void updateWrapperStationSuccessToCheckUpdate() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_CHECK_UPDATE)));

        assertDoesNotThrow(() -> sut.updateWrapperStation(STATION_CODE, buildStationDetails()));

        verify(wrapperStationsRepository).save(argumentCaptorStations.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorStations.getValue().getStatus());
    }

    @Test
    void updateWrapperStationSuccessToFix() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_FIX)));

        assertDoesNotThrow(() -> sut.updateWrapperStation(STATION_CODE, buildStationDetails()));

        verify(wrapperStationsRepository).save(argumentCaptorStations.capture());
        assertEquals(WrapperStatus.TO_CHECK, argumentCaptorStations.getValue().getStatus());
    }

    @Test
    void updateWrapperStationSuccessToFixUpdate() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_FIX_UPDATE)));

        assertDoesNotThrow(() -> sut.updateWrapperStation(STATION_CODE, buildStationDetails()));

        verify(wrapperStationsRepository).save(argumentCaptorStations.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorStations.getValue().getStatus());
    }

    @Test
    void updateWrapperStationSuccessApproved() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.APPROVED)));

        assertDoesNotThrow(() -> sut.updateWrapperStation(STATION_CODE, buildStationDetails()));

        verify(wrapperStationsRepository).save(argumentCaptorStations.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorStations.getValue().getStatus());
    }

    @Test
    void updateWrapperStationSuccessNotFoundCreateNewWrapper() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> sut.updateWrapperStation(STATION_CODE, buildStationDetails()));

        verify(wrapperStationsRepository).save(argumentCaptorStations.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorStations.getValue().getStatus());
    }

    @Test
    void updateValidatedWrapperChannelSuccess() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_CHECK)));
        when(wrapperChannelsRepository.save(any())).thenReturn(buildWrapperEntityChannels(WrapperStatus.TO_CHECK));

        WrapperEntityChannels result = assertDoesNotThrow(() ->
                sut.updateValidatedWrapperChannel(buildChannelDetails(), WrapperStatus.TO_FIX));

        assertNotNull(result);
    }

    @Test
    void updateValidatedWrapperChannelFailNotFound() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.empty());

        ChannelDetails channelDetails = buildChannelDetails();
        AppException e = assertThrows(AppException.class, () ->
                sut.updateValidatedWrapperChannel(channelDetails, WrapperStatus.TO_FIX_UPDATE));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.title, e.getTitle());

        verify(wrapperChannelsRepository, never()).save(any());
    }

    @Test
    void updateValidatedWrapperStationSuccess() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_CHECK)));
        when(wrapperStationsRepository.save(any())).thenReturn(buildWrapperEntityStations(WrapperStatus.TO_CHECK));

        WrapperEntityStations result = assertDoesNotThrow(() ->
                sut.updateValidatedWrapperStation(buildStationDetails(), WrapperStatus.TO_FIX));

        assertNotNull(result);
    }

    @Test
    void updateValidatedWrapperStationFailNotFound() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.empty());

        StationDetails stationDetails = buildStationDetails();
        AppException e = assertThrows(AppException.class, () ->
                sut.updateValidatedWrapperStation(stationDetails, WrapperStatus.TO_FIX_UPDATE));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_STATION_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_STATION_NOT_FOUND.title, e.getTitle());

        verify(wrapperStationsRepository, never()).save(any());
    }

    @Test
    void updateChannelWithOperatorReviewSuccessToCheck() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_CHECK)));

        assertDoesNotThrow(() -> sut.updateChannelWithOperatorReview(CHANNEL_CODE, "note"));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_FIX, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateChannelWithOperatorReviewSuccessToCheckUpdate() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_CHECK_UPDATE)));

        assertDoesNotThrow(() -> sut.updateChannelWithOperatorReview(CHANNEL_CODE, "note"));

        verify(wrapperChannelsRepository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_FIX_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateChannelWithOperatorReviewFailNotFound() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class, () -> sut.updateChannelWithOperatorReview(CHANNEL_CODE, "note"));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.title, e.getTitle());

        verify(wrapperChannelsRepository, never()).save(any());
    }

    @Test
    void findStationByIdSuccess() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.APPROVED)));

        WrapperEntityStations result = assertDoesNotThrow(() -> sut.findStationById(STATION_CODE));

        assertNotNull(result);
    }

    @Test
    void findStationByIdFail() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class, () -> sut.findStationById(STATION_CODE));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_STATION_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_STATION_NOT_FOUND.title, e.getTitle());
    }

    @Test
    void findChannelByIdSuccess() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.APPROVED)));

        WrapperEntityChannels result = assertDoesNotThrow(() -> sut.findChannelById(CHANNEL_CODE));

        assertNotNull(result);
    }

    @Test
    void findChannelByIdFail() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class, () -> sut.findChannelById(CHANNEL_CODE));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.title, e.getTitle());
    }

    @Test
    void findChannelByIdOptionalSuccess() {
        when(wrapperChannelsRepository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.APPROVED)));

        Optional<WrapperEntityChannels> result = assertDoesNotThrow(() -> sut.findChannelByIdOptional(CHANNEL_CODE));

        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    void findStationByIdOptionalSuccess() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations(WrapperStatus.TO_CHECK)));

        Optional<WrapperEntityStations> result = assertDoesNotThrow(() -> sut.findStationByIdOptional(STATION_CODE));

        assertNotNull(result);
        assertTrue(result.isPresent());
    }

    @Test
    void findStationByIdOptionalSuccessEmpty() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.empty());

        Optional<WrapperEntityStations> result = assertDoesNotThrow(() -> sut.findStationByIdOptional(STATION_CODE));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private WrapperEntityStations buildWrapperEntityStations(WrapperStatus wrapperStatus) {
        WrapperEntityStation entity = new WrapperEntityStation();
        entity.setEntity(buildStationDetails());
        entity.setStatus(wrapperStatus);

        List<WrapperEntityStation> entityList = new ArrayList<>();
        entityList.add(entity);
        WrapperEntityStations entities = new WrapperEntityStations();
        entities.setEntities(entityList);
        return entities;
    }

    private StationDetails buildStationDetails() {
        StationDetails stationDetails = new StationDetails();
        stationDetails.setStationCode(STATION_CODE);
        stationDetails.setEnabled(true);
        stationDetails.setVersion(1L);
        return stationDetails;
    }

    private WrapperEntities<ChannelDetails> buildChannelDetailsWrapperEntities(WrapperStatus wrapperStatus) {
        WrapperEntity<ChannelDetails> entity = new WrapperEntity<>();
        entity.setEntity(buildChannelDetails());
        entity.setStatus(wrapperStatus);
        List<WrapperEntity<ChannelDetails>> entityList = new ArrayList<>();
        entityList.add(entity);
        WrapperEntities<ChannelDetails> entities = new WrapperEntities<>();
        entities.setCreatedAt(Instant.now());
        entities.setEntities(entityList);
        return entities;
    }

    private WrapperEntityChannels buildWrapperEntityChannels(WrapperStatus wrapperStatus) {
        WrapperEntityChannel entity = new WrapperEntityChannel();
        entity.setEntity(buildChannelDetails());
        entity.setStatus(wrapperStatus);

        List<WrapperEntityChannel> entityList = new ArrayList<>();
        entityList.add(entity);
        WrapperEntityChannels entities = new WrapperEntityChannels();
        entities.setCreatedAt(Instant.now());
        entities.setEntities(entityList);
        return entities;
    }

    private ChannelDetails buildChannelDetails() {
        ChannelDetails channelDetails = new ChannelDetails();
        channelDetails.setChannelCode(CHANNEL_CODE);
        channelDetails.setEnabled(true);
        channelDetails.setService("service");
        channelDetails.setProtocol(Protocol.HTTPS);
        channelDetails.setPort(2222L);
        channelDetails.setBrokerPspCode("brokerPspCode");
        channelDetails.setProxyPort(24444L);
        channelDetails.setThreadNumber(2L);
        channelDetails.setTimeoutA(22L);
        channelDetails.setTimeoutB(28L);
        channelDetails.setTimeoutC(10L);
        channelDetails.setRedirectPort(6666L);
        return channelDetails;
    }
}
