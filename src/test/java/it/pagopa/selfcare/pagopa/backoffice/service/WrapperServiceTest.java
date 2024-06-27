package it.pagopa.selfcare.pagopa.backoffice.service;

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
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperStationList;
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
    private ArgumentCaptor<WrapperEntities<ChannelDetails>> argumentCaptorChannels;

    @Captor
    private ArgumentCaptor<WrapperEntityStations> argumentCaptorStations;

    @Autowired
    private WrapperService sut;

    @Test
    void getFirstValidChannelCodeV2() {
        Channel channel = new Channel();
        channel.setChannelCode("000001_01");
        channel.setEnabled(true);
        Channels channels = new Channels();
        channels.setChannelList(Collections.singletonList(channel));
        when(apiConfigClient.getChannels(any(), any(), any(), any(), any())).thenReturn(channels);
        WrapperEntities<?> wrapperEntities = new WrapperEntities<>();
        wrapperEntities.setId("000001_01");
        when(repository.findByTypeAndBrokerCode(any(), any(), any())).thenReturn(
                new PageImpl<>(Collections.singletonList(wrapperEntities)));
        sut.getFirstValidChannelCodeV2("000001");
        verify(apiConfigClient).getChannels(any(), any(), any(), any(), any());
        verify(repository).findByTypeAndBrokerCode(any(), any(), any());
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
        when(repository.insert(any(WrapperEntities.class))).thenReturn(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK));

        WrapperEntities<StationDetails> result = assertDoesNotThrow(() ->
                sut.createWrapperStation(buildStationDetails(), WrapperStatus.APPROVED));

        assertNotNull(result);

        verify(repository, never()).findById(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    void createWrapperStationSuccessWithUpdate() {
        when(repository.insert(any(WrapperEntities.class))).thenThrow(DuplicateKeyException.class);
        when(repository.findById(anyString())).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK)));
        when(repository.save(any())).thenReturn(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK));

        WrapperEntities<StationDetails> result = assertDoesNotThrow(() ->
                sut.createWrapperStation(buildStationDetails(), WrapperStatus.APPROVED));

        assertNotNull(result);
    }

    @Test
    void updateWrapperChannelSuccessToCheck() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(repository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessToCheckUpdate() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK_UPDATE)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(repository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessToFix() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_FIX)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(repository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessToFixUpdate() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_FIX_UPDATE)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(repository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelSuccessApproved() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.APPROVED)));

        assertDoesNotThrow(() -> sut.updateWrapperChannel(CHANNEL_CODE, buildChannelDetails()));

        verify(repository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_CHECK_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateWrapperChannelFailNotFound() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.empty());

        ChannelDetails channelDetails = buildChannelDetails();
        AppException e = assertThrows(AppException.class, () -> sut.updateWrapperChannel(CHANNEL_CODE, channelDetails));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.title, e.getTitle());

        verify(repository, never()).save(any());
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
    void updateWrapperStationFailNotFound() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.empty());

        StationDetails stationDetails = buildStationDetails();
        AppException e = assertThrows(AppException.class, () -> sut.updateWrapperStation(STATION_CODE, stationDetails));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_STATION_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_STATION_NOT_FOUND.title, e.getTitle());

        verify(wrapperStationsRepository, never()).save(any());
    }

    @Test
    void updateValidatedWrapperChannelSuccess() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK)));
        when(repository.save(any())).thenReturn(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK));

        WrapperEntities<ChannelDetails> result = assertDoesNotThrow(() ->
                sut.updateValidatedWrapperChannel(buildChannelDetails(), WrapperStatus.TO_FIX));

        assertNotNull(result);
    }

    @Test
    void updateValidatedWrapperChannelFailNotFound() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.empty());

        ChannelDetails channelDetails = buildChannelDetails();
        AppException e = assertThrows(AppException.class, () ->
                sut.updateValidatedWrapperChannel(channelDetails, WrapperStatus.TO_FIX_UPDATE));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.title, e.getTitle());

        verify(repository, never()).save(any());
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
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK)));

        assertDoesNotThrow(() -> sut.updateChannelWithOperatorReview(CHANNEL_CODE, "note"));

        verify(repository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_FIX, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateChannelWithOperatorReviewSuccessToCheckUpdate() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.of(buildChannelDetailsWrapperEntities(WrapperStatus.TO_CHECK_UPDATE)));

        assertDoesNotThrow(() -> sut.updateChannelWithOperatorReview(CHANNEL_CODE, "note"));

        verify(repository).save(argumentCaptorChannels.capture());
        assertEquals(WrapperStatus.TO_FIX_UPDATE, argumentCaptorChannels.getValue().getStatus());
    }

    @Test
    void updateChannelWithOperatorReviewFailNotFound() {
        when(repository.findById(CHANNEL_CODE)).thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class, () -> sut.updateChannelWithOperatorReview(CHANNEL_CODE, "note"));

        assertNotNull(e);
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.WRAPPER_CHANNEL_NOT_FOUND.title, e.getTitle());

        verify(repository, never()).save(any());
    }

    @Test
    void findStationByIdSuccess() {
        when(wrapperStationsRepository.findById(STATION_CODE)).thenReturn(Optional.of(buildWrapperEntityStations()));

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
        WrapperEntityStations entities = new WrapperEntityStations();
        entities.setEntities(Collections.singletonList(entity));
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
