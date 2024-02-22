package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerOrPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProviderDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProviderDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProvidersResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviders;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannels;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceProviderServiceTest {

    @Mock
    private ApiConfigClient apiConfigClientMock;

    @Mock
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClientMock;

    @Mock
    private WrapperService wrapperServiceMock;

    @Mock
    private LegacyPspCodeUtil legacyPspCodeUtil;

    @Mock
    private ModelMapper modelMapperMock;

    @InjectMocks
    private PaymentServiceProviderService sut;

    @Test
    void createPSPDirectTrue() {
        when(apiConfigClientMock.createPaymentServiceProvider(any()))
                .thenReturn(getPaymentServiceProviderDetails());

        PaymentServiceProviderDetailsDto pspDetailsDto = new PaymentServiceProviderDetailsDto();
        pspDetailsDto.setTaxCode("tax-code");
        pspDetailsDto.setAbi("TESTABI");
        PaymentServiceProviderDetailsResource result =
                assertDoesNotThrow(() -> sut.createPSP(pspDetailsDto, true));

        assertNotNull(result);
        verify(apiConfigClientMock).createBrokerPsp(any());
    }

    @Test
    void createPSPWithDirectFalse() {
        when(apiConfigClientMock.createPaymentServiceProvider(any()))
                .thenReturn(getPaymentServiceProviderDetails());

        PaymentServiceProviderDetailsDto pspDetailsDto = new PaymentServiceProviderDetailsDto();
        pspDetailsDto.setTaxCode("tax-code");
        pspDetailsDto.setBic("TESTBIC");
        PaymentServiceProviderDetailsResource result =
                assertDoesNotThrow(() -> sut.createPSP(pspDetailsDto, false));

        assertNotNull(result);
        verify(apiConfigClientMock, never()).createBrokerPsp(any());
    }

    @Test
    void updatePSP() {
        when(apiConfigClientMock.updatePSP(anyString(), any()))
                .thenReturn(getPaymentServiceProviderDetails());
        PaymentServiceProviderDetailsResource result =
                assertDoesNotThrow(() -> sut.updatePSP("psp-code", new PaymentServiceProviderDetailsDto()));

        assertNotNull(result);
    }

    @Test
    void getPaymentServiceProviders() {
        when(apiConfigClientMock.getPaymentServiceProviders(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(PaymentServiceProviders.builder()
                        .pageInfo(new PageInfo())
                        .paymentServiceProviderList(new ArrayList<>())
                        .build());
        PaymentServiceProvidersResource result =
                assertDoesNotThrow(() -> sut.getPaymentServiceProviders(10, 0, "psp-code", "tax-code", "name"));

        assertNotNull(result);
    }

    @Test
    void getBrokerAndPspDetails() {
        BrokerPspDetails brokerPspDetails = new BrokerPspDetails(true);
        when(apiConfigClientMock.getBrokerPsp(anyString()))
                .thenReturn(brokerPspDetails);
        PaymentServiceProviderDetails paymentServiceProviderDetails = getPaymentServiceProviderDetails();
        when(apiConfigClientMock.getPSPDetails(anyString()))
                .thenReturn(paymentServiceProviderDetails);

        BrokerOrPspDetailsResource result =
                assertDoesNotThrow(() -> sut.getBrokerAndPspDetails("broker-psp-code"));

        assertNotNull(result);
        assertNotNull(result.getBrokerPspDetailsResource());
        assertNotNull(result.getPaymentServiceProviderDetailsResource());
        assertTrue(result.getBrokerPspDetailsResource().getExtendedFaultBean());
        assertEquals(paymentServiceProviderDetails.getAbi(), result.getPaymentServiceProviderDetailsResource().getAbi());
        assertEquals(paymentServiceProviderDetails.getBic(), result.getPaymentServiceProviderDetailsResource().getBic());
    }

    @Test
    void getBrokerAndPspDetailsWithErrorOnGetBrokerPsp() {
        when(apiConfigClientMock.getBrokerPsp(anyString()))
                .thenThrow(FeignException.NotFound.class);
        PaymentServiceProviderDetails paymentServiceProviderDetails = getPaymentServiceProviderDetails();
        when(apiConfigClientMock.getPSPDetails(anyString()))
                .thenReturn(paymentServiceProviderDetails);

        BrokerOrPspDetailsResource result =
                assertDoesNotThrow(() -> sut.getBrokerAndPspDetails("broker-psp-code"));

        assertNotNull(result);
        assertNull(result.getBrokerPspDetailsResource());
        assertNotNull(result.getPaymentServiceProviderDetailsResource());
        assertEquals(paymentServiceProviderDetails.getAbi(), result.getPaymentServiceProviderDetailsResource().getAbi());
        assertEquals(paymentServiceProviderDetails.getBic(), result.getPaymentServiceProviderDetailsResource().getBic());
    }

    @Test
    void getBrokerAndPspDetailsWithErrorOnGetPSPDetails() {
        BrokerPspDetails brokerPspDetails = new BrokerPspDetails(true);
        when(apiConfigClientMock.getBrokerPsp(anyString()))
                .thenReturn(brokerPspDetails);
        when(apiConfigClientMock.getPSPDetails(anyString()))
                .thenThrow(FeignException.NotFound.class);

        BrokerOrPspDetailsResource result =
                assertDoesNotThrow(() -> sut.getBrokerAndPspDetails("broker-psp-code"));

        assertNotNull(result);
        assertNotNull(result.getBrokerPspDetailsResource());
        assertNull(result.getPaymentServiceProviderDetailsResource());
        assertTrue(result.getBrokerPspDetailsResource().getExtendedFaultBean());
    }

    @Test
    void getBrokerAndPspDetailsWithErrorOnBothApiCalls() {
        when(apiConfigClientMock.getBrokerPsp(anyString()))
                .thenThrow(FeignException.NotFound.class);
        when(apiConfigClientMock.getPSPDetails(anyString()))
                .thenThrow(FeignException.NotFound.class);

        AppException e =
                assertThrows(AppException.class, () -> sut.getBrokerAndPspDetails("broker-psp-code"));

        assertNotNull(e);
        assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpStatus().value());
    }

    @Test
    void getPSPChannels() {
        when(apiConfigSelfcareIntegrationClientMock.getPspChannels(anyString()))
                .thenReturn(new PspChannels(new ArrayList<>()));
        PspChannelsResource result =
                assertDoesNotThrow(() -> sut.getPSPChannels("psp-code"));

        assertNotNull(result);
    }

    @Test
    void updatePSPChannel() {
        when(apiConfigClientMock.updatePaymentServiceProvidersChannels(anyString(), anyString(), any()))
                .thenReturn(new PspChannelPaymentTypes(new ArrayList<>()));
        when(legacyPspCodeUtil.retrievePspCode(any(),eq(false))).thenReturn("psp-code");
        PspChannelPaymentTypesResource result =
                assertDoesNotThrow(() -> sut.updatePSPChannel("tax-code", "channel-code", new PspChannelPaymentTypes()));

        assertNotNull(result);
    }

    @Test
    void deletePSPChannel() {
        assertDoesNotThrow(() -> sut.deletePSPChannel("psp-code", "channel-code"));
    }

    @Test
    void getFirstValidChannelCodeWithV2True() {
        String channelCodeV2 = "channel-code-v2";
        when(wrapperServiceMock.getFirstValidCodeV2(anyString()))
                .thenReturn(channelCodeV2);
        ChannelCodeResource result =
                assertDoesNotThrow(() -> sut.getFirstValidChannelCode("psp-code", true));

        assertNotNull(result);
        assertEquals(channelCodeV2, result.getChannelCode());
        verify(wrapperServiceMock).getFirstValidCodeV2(anyString());
        verify(apiConfigClientMock, never()).getChannels(anyInt(), anyInt(), anyString(), eq(null), anyString());
    }

    @Test
    void getFirstValidChannelCodeWithV2False() {
        String channelCode = "channel_23";
        Channels channels = new Channels(
                Collections.singletonList(
                        Channel.builder()
                                .channelCode("channel_23")
                                .build())
                , new PageInfo());
        when(apiConfigClientMock.getChannels(anyInt(), anyInt(), anyString(), eq(null), anyString()))
                .thenReturn(channels);
        ChannelCodeResource result =
                assertDoesNotThrow(() -> sut.getFirstValidChannelCode("psp-code", false));

        assertNotNull(result);
        assertNotEquals(channelCode, result.getChannelCode());
        verify(wrapperServiceMock, never()).getFirstValidCodeV2(anyString());
        verify(apiConfigClientMock).getChannels(anyInt(), anyInt(), anyString(), eq(null), anyString());
    }

    private PaymentServiceProviderDetails getPaymentServiceProviderDetails() {
        return PaymentServiceProviderDetails.builder()
                .abi("abi")
                .agidPsp(true)
                .bic("bic")
                .build();
    }
}