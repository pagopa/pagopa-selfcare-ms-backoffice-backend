package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannel;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityChannels;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelPspListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.WrapperChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelPspList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.WrapperChannelList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.ConfigurationStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ChannelService.class})
class ChannelServiceTest {

    private static final String CHANNEL_CODE = "channelCode";
    private static final String BROKER_CODE = "brokerCode";

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private WrapperService wrapperService;

    @MockBean
    private JiraServiceManagerClient jsmClient;

    @MockBean
    private AwsSesClient awsSesClient;

    @Autowired
    private ChannelService sut;

    @Test
    void createChannelToBeValidatedSuccess() {
        when(wrapperService.createWrapperChannel(any(ChannelDetails.class), any()))
                .thenReturn(buildChannelDetailsWrapperEntities());

        WrapperEntities<ChannelDetails> result = assertDoesNotThrow(() ->
                sut.createChannelToBeValidated(buildWrapperChannelDetailsDto()));

        assertNotNull(result);
        assertTrue(result.getEntities().get(0).getEntity().getFlagStandin());

        verify(jsmClient).createTicket(anyString(), anyString());
    }

    @Test
    void updateChannelToBeValidatedSuccess() {
        when(wrapperService.updateWrapperChannel(anyString(), any(ChannelDetails.class)))
                .thenReturn(buildWrapperEntityChannels(WrapperStatus.APPROVED));

        ChannelDetailsResource result = assertDoesNotThrow(() ->
                sut.updateChannelToBeValidated(CHANNEL_CODE, buildChannelDetailsDto()));

        assertNotNull(result);
        assertTrue(result.getFlagStandin());

        verify(jsmClient).createTicket(anyString(), anyString());
    }

    @Test
    void validateChannelCreationSuccess() {
        WrapperEntityChannels wrapperEntities = buildWrapperEntityChannels(WrapperStatus.APPROVED);

        when(wrapperService.updateValidatedWrapperChannel(any(ChannelDetails.class), any()))
                .thenReturn(wrapperEntities);
        when(apiConfigClient.createChannelPaymentType(any(), anyString()))
                .thenReturn(new PspChannelPaymentTypes());

        ChannelDetailsResource result = assertDoesNotThrow(() -> sut.validateChannelCreation(buildChannelDetailsDto()));

        assertNotNull(result);

        ChannelDetails expected = wrapperEntities.getEntities().get(0).getEntity();
        assertEquals(expected.getChannelCode(), result.getChannelCode());
        assertEquals(expected.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(expected.getTimeoutA(), result.getTimeoutA());
        assertEquals(expected.getTimeoutB(), result.getTimeoutB());
        assertEquals(expected.getTimeoutC(), result.getTimeoutC());
        assertEquals(expected.getProtocol(), result.getProtocol());
        assertTrue(result.getFlagStandin());

        verify(apiConfigClient).createChannel(any());
        verify(awsSesClient).sendEmail(any());
    }

    @Test
    void validateChannelUpdateSuccess() {
        WrapperEntities<ChannelDetails> wrapperEntities = buildChannelDetailsWrapperEntities();

        when(apiConfigClient.updateChannel(any(), anyString()))
                .thenReturn(buildChannelDetails());

        ChannelDetailsResource result = assertDoesNotThrow(() ->
                sut.validateChannelUpdate(CHANNEL_CODE, buildChannelDetailsDto()));

        assertNotNull(result);

        ChannelDetails expected = wrapperEntities.getEntities().get(0).getEntity();
        assertEquals(expected.getChannelCode(), result.getChannelCode());
        assertEquals(expected.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(expected.getTimeoutA(), result.getTimeoutA());
        assertEquals(expected.getTimeoutB(), result.getTimeoutB());
        assertEquals(expected.getTimeoutC(), result.getTimeoutC());
        assertEquals(expected.getProtocol(), result.getProtocol());
        assertTrue(result.getFlagStandin());

        verify(wrapperService).update(any(ChannelDetails.class), anyString(), anyString(), eq(null));
        verify(awsSesClient).sendEmail(any());
    }

    @Test
    void getChannelsSuccessActiveWithWrapperFound() {
        Channels channels = buildChannels();
        when(apiConfigClient.getChannels(CHANNEL_CODE, BROKER_CODE, "DESC", 10, 0))
                .thenReturn(channels);
        when(wrapperService.findChannelByIdOptional(CHANNEL_CODE))
                .thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.APPROVED)));

        WrapperChannelsResource result = assertDoesNotThrow(() ->
                sut.getChannels(ConfigurationStatus.ACTIVE, CHANNEL_CODE, BROKER_CODE, 10, 0));

        assertNotNull(result);
        assertNotNull(result.getChannelList());
        assertEquals(1, result.getChannelList().size());

        Channel expected = channels.getChannelList().get(0);
        assertEquals(expected.getChannelCode(), result.getChannelList().get(0).getChannelCode());
        assertEquals(expected.getBrokerDescription(), result.getChannelList().get(0).getBrokerDescription());
        assertEquals(expected.getEnabled(), result.getChannelList().get(0).getEnabled());

        verify(wrapperService, never()).getWrapperChannels(CHANNEL_CODE, BROKER_CODE, 0, 10);
    }

    @Test
    void getChannelsSuccessActiveWithWrapperNotFound() {
        Channels channels = buildChannels();
        when(apiConfigClient.getChannels(CHANNEL_CODE, BROKER_CODE, "DESC", 10, 0))
                .thenReturn(channels);
        when(wrapperService.findChannelByIdOptional(CHANNEL_CODE)).thenReturn(Optional.empty());

        WrapperChannelsResource result = assertDoesNotThrow(() ->
                sut.getChannels(ConfigurationStatus.ACTIVE, CHANNEL_CODE, BROKER_CODE, 10, 0));

        assertNotNull(result);
        assertNotNull(result.getChannelList());
        assertEquals(1, result.getChannelList().size());

        Channel expected = channels.getChannelList().get(0);
        assertEquals(expected.getChannelCode(), result.getChannelList().get(0).getChannelCode());
        assertEquals(expected.getBrokerDescription(), result.getChannelList().get(0).getBrokerDescription());
        assertEquals(expected.getEnabled(), result.getChannelList().get(0).getEnabled());

        verify(wrapperService, never()).getWrapperChannels(CHANNEL_CODE, BROKER_CODE, 0, 10);
    }

    @Test
    void getChannelsSuccessToBeValidated() {
        WrapperChannelList wrapperChannelList = buildWrapperChannelList();

        when(wrapperService.getWrapperChannels(CHANNEL_CODE, BROKER_CODE, 10, 0))
                .thenReturn(wrapperChannelList);

        WrapperChannelsResource result = assertDoesNotThrow(() ->
                sut.getChannels(ConfigurationStatus.TO_BE_VALIDATED, CHANNEL_CODE, BROKER_CODE, 10, 0));

        assertNotNull(result);
        assertNotNull(result.getChannelList());
        assertEquals(1, result.getChannelList().size());

        ChannelDetails expected = wrapperChannelList.getWrapperEntities().get(0).getEntities().get(0).getEntity();
        assertEquals(expected.getChannelCode(), result.getChannelList().get(0).getChannelCode());
        assertEquals(expected.getBrokerDescription(), result.getChannelList().get(0).getBrokerDescription());
        assertEquals(expected.getEnabled(), result.getChannelList().get(0).getEnabled());

        verify(apiConfigClient, never()).getChannels(CHANNEL_CODE, BROKER_CODE, "DESC", 10, 0);
    }

    @Test
    void getChannelDetailsSuccessFromWrapper() {
        ChannelDetails expected = buildChannelDetails();

        when(wrapperService.findChannelById(CHANNEL_CODE))
                .thenReturn(buildWrapperEntityChannels(WrapperStatus.APPROVED));

        ChannelDetailsResource result = assertDoesNotThrow(() ->
                sut.getChannelDetails(CHANNEL_CODE, ConfigurationStatus.TO_BE_VALIDATED));

        assertNotNull(result);

        assertEquals(expected.getChannelCode(), result.getChannelCode());
        assertEquals(expected.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(expected.getTimeoutA(), result.getTimeoutA());
        assertEquals(expected.getTimeoutB(), result.getTimeoutB());
        assertEquals(expected.getTimeoutC(), result.getTimeoutC());
        assertEquals(expected.getProtocol(), result.getProtocol());
        assertTrue(result.getFlagStandin());

        verify(apiConfigClient, never()).getChannelDetails(CHANNEL_CODE);
        verify(apiConfigClient, never()).getChannelPaymentTypes(CHANNEL_CODE);
    }

    @Test
    void getChannelDetailsSuccessFromWrapperFallbackToApiConfig() {
        ChannelDetails expected = buildChannelDetails();

        when(wrapperService.findChannelById(CHANNEL_CODE)).thenThrow(AppException.class);
        when(apiConfigClient.getChannelDetails(CHANNEL_CODE)).thenReturn(expected);
        when(apiConfigClient.getChannelPaymentTypes(CHANNEL_CODE)).thenReturn(new PspChannelPaymentTypes());
        when(wrapperService.findChannelByIdOptional(CHANNEL_CODE))
                .thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.APPROVED)));

        ChannelDetailsResource result = assertDoesNotThrow(() ->
                sut.getChannelDetails(CHANNEL_CODE, ConfigurationStatus.TO_BE_VALIDATED));

        assertNotNull(result);

        assertEquals(CHANNEL_CODE, result.getChannelCode());
        assertEquals(expected.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(expected.getTimeoutA(), result.getTimeoutA());
        assertEquals(expected.getTimeoutB(), result.getTimeoutB());
        assertEquals(expected.getTimeoutC(), result.getTimeoutC());
        assertEquals(expected.getProtocol(), result.getProtocol());
        assertFalse(result.getPendingUpdate());
        assertTrue(result.getFlagStandin());
    }

    @Test
    void getChannelDetailsSuccessFromApiConfigWithNoPendingUpdateNoWrapperFound() {
        ChannelDetails expected = buildChannelDetails();

        when(apiConfigClient.getChannelDetails(CHANNEL_CODE)).thenReturn(expected);
        when(apiConfigClient.getChannelPaymentTypes(CHANNEL_CODE)).thenReturn(new PspChannelPaymentTypes());
        when(wrapperService.findChannelByIdOptional(CHANNEL_CODE)).thenReturn(Optional.empty());

        ChannelDetailsResource result = assertDoesNotThrow(() -> sut.getChannelDetails(CHANNEL_CODE, ConfigurationStatus.ACTIVE));

        assertNotNull(result);

        assertEquals(CHANNEL_CODE, result.getChannelCode());
        assertEquals(expected.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(expected.getTimeoutA(), result.getTimeoutA());
        assertEquals(expected.getTimeoutB(), result.getTimeoutB());
        assertEquals(expected.getTimeoutC(), result.getTimeoutC());
        assertEquals(expected.getProtocol(), result.getProtocol());
        assertFalse(result.getPendingUpdate());
        assertTrue(result.getFlagStandin());
    }

    @Test
    void getChannelDetailsSuccessFromApiConfigWithNoPendingUpdate() {
        ChannelDetails expected = buildChannelDetails();

        when(apiConfigClient.getChannelDetails(CHANNEL_CODE)).thenReturn(expected);
        when(apiConfigClient.getChannelPaymentTypes(CHANNEL_CODE)).thenReturn(new PspChannelPaymentTypes());
        when(wrapperService.findChannelByIdOptional(CHANNEL_CODE))
                .thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.APPROVED)));

        ChannelDetailsResource result = assertDoesNotThrow(() -> sut.getChannelDetails(CHANNEL_CODE, ConfigurationStatus.ACTIVE));

        assertNotNull(result);

        assertEquals(CHANNEL_CODE, result.getChannelCode());
        assertEquals(expected.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(expected.getTimeoutA(), result.getTimeoutA());
        assertEquals(expected.getTimeoutB(), result.getTimeoutB());
        assertEquals(expected.getTimeoutC(), result.getTimeoutC());
        assertEquals(expected.getProtocol(), result.getProtocol());
        assertFalse(result.getPendingUpdate());
        assertTrue(result.getFlagStandin());
    }

    @Test
    void getChannelDetailsSuccessFromApiConfigWithPendingUpdate() {
        ChannelDetails expected = buildChannelDetails();

        when(apiConfigClient.getChannelDetails(CHANNEL_CODE)).thenReturn(expected);
        when(apiConfigClient.getChannelPaymentTypes(CHANNEL_CODE)).thenReturn(new PspChannelPaymentTypes());
        when(wrapperService.findChannelByIdOptional(CHANNEL_CODE))
                .thenReturn(Optional.of(buildWrapperEntityChannels(WrapperStatus.TO_CHECK)));

        ChannelDetailsResource result = assertDoesNotThrow(() -> sut.getChannelDetails(CHANNEL_CODE, ConfigurationStatus.ACTIVE));

        assertNotNull(result);

        assertEquals(CHANNEL_CODE, result.getChannelCode());
        assertEquals(expected.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(expected.getTimeoutA(), result.getTimeoutA());
        assertEquals(expected.getTimeoutB(), result.getTimeoutB());
        assertEquals(expected.getTimeoutC(), result.getTimeoutC());
        assertEquals(expected.getProtocol(), result.getProtocol());
        assertTrue(result.getPendingUpdate());
        assertTrue(result.getFlagStandin());
    }

    @Test
    void getPaymentTypesByChannelSuccess() {
        when(apiConfigClient.getChannelPaymentTypes(CHANNEL_CODE)).thenReturn(new PspChannelPaymentTypes());

        PspChannelPaymentTypesResource result = assertDoesNotThrow(() -> sut.getPaymentTypesByChannel(CHANNEL_CODE));

        assertNotNull(result);
    }

    @Test
    void createPaymentTypeOnChannelSuccess() {
        PspChannelPaymentTypes paymentTypes = new PspChannelPaymentTypes();
        paymentTypes.setPaymentTypeList(Collections.singletonList("paymentType"));

        when(apiConfigClient.createChannelPaymentType(paymentTypes, CHANNEL_CODE)).thenReturn(paymentTypes);

        PspChannelPaymentTypesResource result = assertDoesNotThrow(() ->
                sut.createPaymentTypeOnChannel(paymentTypes, CHANNEL_CODE));

        assertNotNull(result);
    }

    @Test
    void deletePaymentTypeOnChannelSuccess() {
        assertDoesNotThrow(() -> sut.deletePaymentTypeOnChannel(CHANNEL_CODE, "paymentType"));

        verify(apiConfigClient).deleteChannelPaymentType(CHANNEL_CODE, "paymentType");
    }

    @Test
    void deleteChannelSuccess() {
        assertDoesNotThrow(() -> sut.deleteChannel(CHANNEL_CODE));

        verify(apiConfigClient).deleteChannel(CHANNEL_CODE);
    }

    @Test
    void getChannelsInCSVFileSuccess() {
        assertDoesNotThrow(() -> sut.getChannelsInCSVFile(mock(HttpServletResponse.class)));

        verify(apiConfigClient).getChannelsCSV();
    }

    @Test
    void getPSPsByChannelSuccess() {
        when(apiConfigClient.getChannelPaymentServiceProviders(CHANNEL_CODE, 10, 0, "pspName"))
                .thenReturn(new ChannelPspList());

        ChannelPspListResource result = assertDoesNotThrow(() ->
                sut.getPSPsByChannel(10, 0, CHANNEL_CODE, "pspName"));

        assertNotNull(result);
    }

    @Test
    void updateWrapperStationWithOperatorReviewSuccess() {
        when(wrapperService.updateChannelWithOperatorReview(anyString(), anyString()))
                .thenReturn(buildWrapperEntityChannels(WrapperStatus.APPROVED));

        ChannelDetailsResource result = assertDoesNotThrow(() -> sut.updateWrapperChannelWithOperatorReview(
                CHANNEL_CODE, "brokerCode", "nota"));

        assertNotNull(result);

        verify(awsSesClient).sendEmail(any());
    }

    @Test
    void updateWrapperStationWithOperatorReviewFail() {
        when(wrapperService.updateChannelWithOperatorReview(anyString(), anyString()))
                .thenThrow(AppException.class);

        AppException e = assertThrows(AppException.class,
                () -> sut.updateWrapperChannelWithOperatorReview(
                        CHANNEL_CODE, "brokerCode", "nota"));

        assertNotNull(e);

        verify(awsSesClient, never()).sendEmail(any());
    }

    private WrapperChannelDetailsDto buildWrapperChannelDetailsDto() {
        return WrapperChannelDetailsDto.builder()
                .channelCode(CHANNEL_CODE)
                .brokerDescription("brokerDescription")
                .brokerPspCode("brokerPspCode")
                .targetHost("targetHost")
                .targetPort(8088L)
                .targetPath("targetPath")
                .redirectProtocol(Protocol.HTTPS)
                .paymentTypeList(Collections.emptyList())
                .validationUrl("validationUrl")
                .status(WrapperStatus.TO_CHECK)
                .note("note")
                .build();
    }

    private ChannelDetailsDto buildChannelDetailsDto() {
        return ChannelDetailsDto.builder()
                .channelCode(CHANNEL_CODE)
                .brokerDescription("brokerDescription")
                .brokerPspCode("brokerPspCode")
                .targetHost("targetHost")
                .targetPort(8088L)
                .targetPath("targetPath")
                .redirectProtocol(Protocol.HTTPS)
                .paymentTypeList(Collections.emptyList())
                .validationUrl("validationUrl")
                .status(WrapperStatus.TO_CHECK)
                .note("note")
                .build();
    }

    private WrapperChannelList buildWrapperChannelList() {
        var entities = buildWrapperEntityChannels(WrapperStatus.APPROVED);
        WrapperChannelList wrapperEntitiesList = new WrapperChannelList();
        wrapperEntitiesList.setWrapperEntities(Collections.singletonList(entities));
        wrapperEntitiesList.setPageInfo(PageInfo.builder()
                .itemsFound(1)
                .totalPages(1)
                .limit(10)
                .page(0)
                .totalItems(1L)
                .build());
        return wrapperEntitiesList;
    }

    private WrapperEntities<ChannelDetails> buildChannelDetailsWrapperEntities() {
        WrapperEntity<ChannelDetails> entity = new WrapperEntity<>();
        entity.setEntity(buildChannelDetails());
        WrapperEntities<ChannelDetails> entities = new WrapperEntities<>();
        entities.setCreatedAt(Instant.now());
        entities.setEntities(Collections.singletonList(entity));
        return entities;
    }

    private WrapperEntityChannels buildWrapperEntityChannels(WrapperStatus wrapperStatus) {
        WrapperEntityChannel entity = new WrapperEntityChannel();
        entity.setEntity(buildChannelDetails());
        entity.setStatus(wrapperStatus);
        WrapperEntityChannels entities = new WrapperEntityChannels();
        entities.setCreatedAt(Instant.now());
        entities.setStatus(wrapperStatus);
        entities.setEntities(Collections.singletonList(entity));
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
        channelDetails.setFlagStandin(true);
        return channelDetails;
    }

    private Channels buildChannels() {
        return Channels.builder()
                .channelList(Collections.singletonList(
                        Channel.builder()
                                .channelCode(CHANNEL_CODE)
                                .brokerDescription("brokerDescription")
                                .enabled(true)
                                .build()
                ))
                .build();
    }
}
