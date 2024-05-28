package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = WrapperService.class)
class WrapperServiceTest {

    private static final String STATION_CODE = "stationCode";
    private static final String BROKER_CODE = "brokerCode";
    private static final int PAGE = 0;
    private static final int LIMIT = 10;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private WrapperRepository repository;

    @MockBean
    private AuditorAware<String> auditorAware;

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
        WrapperEntities wrapperEntities = new WrapperEntities();
        wrapperEntities.setId("000001_01");
        when(repository.findByTypeAndBrokerCode(any(), any(), any())).thenReturn(
                new PageImpl<>(Collections.singletonList(wrapperEntities)));
        sut.getFirstValidChannelCodeV2("000001");
        verify(apiConfigClient).getChannels(any(), any(), any(), any(), any());
        verify(repository).findByTypeAndBrokerCode(any(), any(), any());
    }

    @Test
    void getWrapperStationsWithStationCodeSuccess() {
        when(repository.findByIdLikeAndTypeAndBrokerCodeAndStatusNot(
                eq(STATION_CODE),
                eq(WrapperType.STATION),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any())
        ).thenReturn(new PageImpl<>(Collections.singletonList(buildStationDetailsWrapperEntities())));

        WrapperEntitiesList result = assertDoesNotThrow(() ->
                sut.getWrapperStations(STATION_CODE, BROKER_CODE, PAGE, LIMIT));

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
        when(repository.findByTypeAndBrokerCodeAndStatusNot(
                eq(WrapperType.STATION),
                eq(BROKER_CODE),
                eq(WrapperStatus.APPROVED),
                any())
        ).thenReturn(new PageImpl<>(Collections.singletonList(buildStationDetailsWrapperEntities())));

        WrapperEntitiesList result = assertDoesNotThrow(() ->
                sut.getWrapperStations(null, BROKER_CODE, PAGE, LIMIT));

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
    void updateStationWithOperatorReviewSuccess() {
        when(repository.findById(STATION_CODE)).thenReturn(Optional.of(buildStationDetailsWrapperEntities()));

        assertDoesNotThrow(() -> sut.updateStationWithOperatorReview(STATION_CODE, "operator review note"));

        verify(auditorAware).getCurrentAuditor();
        verify(repository).save(any());
    }

    @Test
    void updateStationWithOperatorReviewSuccessFailNotFound() {
        when(repository.findById(STATION_CODE)).thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class,
                () -> sut.updateStationWithOperatorReview(STATION_CODE, "operator review note"));

        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());

        verify(auditorAware, never()).getCurrentAuditor();
        verify(repository, never()).save(any());
    }

    private @NotNull WrapperEntities<StationDetails> buildStationDetailsWrapperEntities() {
        WrapperEntity<StationDetails> entity = new WrapperEntity<>();
        entity.setEntity(buildStationDetails());
        entity.setStatus(WrapperStatus.TO_CHECK);
        WrapperEntities<StationDetails> entities = new WrapperEntities<>();
        entities.setEntities(Collections.singletonList(entity));
        return entities;
    }

    private @NotNull StationDetails buildStationDetails() {
        StationDetails stationDetails = new StationDetails();
        stationDetails.setStationCode(STATION_CODE);
        stationDetails.setEnabled(true);
        stationDetails.setVersion(1L);
        return stationDetails;
    }
}