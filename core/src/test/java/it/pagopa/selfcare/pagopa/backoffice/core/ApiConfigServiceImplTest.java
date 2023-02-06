package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannelPaymentTypes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApiConfigServiceImpl.class)
class ApiConfigServiceImplTest {

    @Autowired
    private ApiConfigServiceImpl apiConfigService;

    @MockBean
    private ApiConfigConnector apiConfigConnectorMock;

    @Test
    void getChannels_nullPage() {
        //given
        final Integer limit = 1;
        final Integer page = null;
        final String code = "code";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        //when
        apiConfigConnectorMock.getChannels(limit, page, code, sort, xRequestId);
        //then
        verify(apiConfigConnectorMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getChannels() {
        //given
        final Integer limit = 1;
        final Integer page = 1;
        final String code = "code";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        Channels channelsMock = mock(Channels.class);
        when(apiConfigConnectorMock.getChannels(any(), any(), any(), any(), any()))
                .thenReturn(channelsMock);
        //when
        Channels channels = apiConfigService.getChannels(limit, page, code, sort, xRequestId);
        //then
        assertNotNull(channels);
        assertEquals(channelsMock, channels);
        reflectionEqualsByName(channelsMock, channels);
        verify(apiConfigConnectorMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getPspChannel() {
        //given

        final String pspcode = "pspcode";
        final String xRequestId = "xRequestId";
        PspChannels pspChannels = mock(PspChannels.class);
        when(apiConfigConnectorMock.getPspChannels(any(), any()))
                .thenReturn(pspChannels);
        //when
        PspChannels response = apiConfigService.getPspChannels(pspcode, xRequestId);
        //then
        assertNotNull(response);
        assertEquals(pspChannels, response);
        reflectionEqualsByName(pspChannels, response);
        verify(apiConfigConnectorMock, times(1))
                .getPspChannels(pspcode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void createChannel() {
        //given

        final String xRequestId = "xRequestId";
        ChannelDetails channelDetailsMock = mock(ChannelDetails.class);

        when(apiConfigConnectorMock.createChannel(any(),any()))
                .thenReturn(channelDetailsMock);
        //when
        ChannelDetails channelDetailsRes = apiConfigService.createChannel(channelDetailsMock, xRequestId);
        //then
        assertNotNull(channelDetailsRes);
        assertEquals(channelDetailsRes, channelDetailsMock);
        reflectionEqualsByName(channelDetailsRes, channelDetailsMock);
        verify(apiConfigConnectorMock, times(1))
                .createChannel(channelDetailsMock,xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void createChannelPaymentType() {
        //given
        final String xRequestId = "xRequestId";
        final String channelCode = "channelCode";

        PspChannelPaymentTypes pspChannelPaymentTypesMock = mock(PspChannelPaymentTypes.class);
        pspChannelPaymentTypesMock.setPaymentTypeList(List.of("paymentType"));
        when(apiConfigConnectorMock.createChannelPaymentType(any(),anyString(),anyString()))
                .thenReturn(pspChannelPaymentTypesMock);
        //when
        PspChannelPaymentTypes pspChannelPaymentTypesRes = apiConfigService.createChannelPaymentType(pspChannelPaymentTypesMock,channelCode, xRequestId);
        //then
        assertNotNull(pspChannelPaymentTypesRes);
        assertEquals(pspChannelPaymentTypesRes, pspChannelPaymentTypesMock);
        reflectionEqualsByName(pspChannelPaymentTypesRes, pspChannelPaymentTypesMock);
        verify(apiConfigConnectorMock, times(1))
                .createChannelPaymentType(pspChannelPaymentTypesMock,channelCode,xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }
}
