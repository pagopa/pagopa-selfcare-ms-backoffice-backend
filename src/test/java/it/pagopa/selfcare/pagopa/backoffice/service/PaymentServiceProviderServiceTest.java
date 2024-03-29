package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceProviderServiceTest {

    public static final String BROKER_PSP_CODE = "broker-psp-code";
    public static final String TAX_CODE = "tax-code";
    public static final String CHANNEL_CODE = "channel-code";
    public static final String PSP_CODE = "psp-code";
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
        pspDetailsDto.setTaxCode(TAX_CODE);
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
        pspDetailsDto.setTaxCode(TAX_CODE);
        pspDetailsDto.setBic("TESTBIC");
        PaymentServiceProviderDetailsResource result =
                assertDoesNotThrow(() -> sut.createPSP(pspDetailsDto, false));

        assertNotNull(result);
        verify(apiConfigClientMock, never()).createBrokerPsp(any());
    }

    @Test
    void createPSPErrorOnMissingAbiOrBic() {
        PaymentServiceProviderDetailsDto pspDetailsDto = new PaymentServiceProviderDetailsDto();
        pspDetailsDto.setTaxCode("tax-code");
        assertThrows(AppException.class, () -> sut.createPSP(pspDetailsDto, false));
        verify(apiConfigClientMock, never()).createBrokerPsp(any());
    }

    @Test
    void updatePSP() {
        when(apiConfigClientMock.updatePSP(eq(PSP_CODE), any()))
                .thenReturn(getPaymentServiceProviderDetails());
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE,false)).thenReturn(PSP_CODE);
        PaymentServiceProviderDetailsResource result =
                assertDoesNotThrow(() -> sut.updatePSP(TAX_CODE, new PaymentServiceProviderDetailsDto()));

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
                assertDoesNotThrow(() -> sut.getPaymentServiceProviders(10, 0, PSP_CODE, TAX_CODE, "name"));

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
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE,false)).thenReturn(BROKER_PSP_CODE);

        BrokerOrPspDetailsResource result =
                assertDoesNotThrow(() -> sut.getBrokerAndPspDetails(TAX_CODE));

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
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE,false)).thenReturn(BROKER_PSP_CODE);
        BrokerOrPspDetailsResource result =
                assertDoesNotThrow(() -> sut.getBrokerAndPspDetails(TAX_CODE));

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
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE,false)).thenReturn(BROKER_PSP_CODE);
        BrokerOrPspDetailsResource result =
                assertDoesNotThrow(() -> sut.getBrokerAndPspDetails(TAX_CODE));

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
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE,false)).thenReturn(BROKER_PSP_CODE);
        AppException e =
                assertThrows(AppException.class, () -> sut.getBrokerAndPspDetails(TAX_CODE));

        assertNotNull(e);
        assertEquals(HttpStatus.SC_NOT_FOUND, e.getHttpStatus().value());
    }

    @Test
    void getPSPChannels() {
        when(apiConfigSelfcareIntegrationClientMock.getPspChannels(anyString()))
                .thenReturn(new PspChannels(new ArrayList<>()));
        PspChannelsResource result =
                assertDoesNotThrow(() -> sut.getPSPChannels(PSP_CODE));

        assertNotNull(result);
    }

    @Test
    void updatePSPChannel() {
        when(apiConfigClientMock.updatePaymentServiceProvidersChannels(anyString(), anyString(), any()))
                .thenReturn(new PspChannelPaymentTypes(new ArrayList<>()));
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE,false)).thenReturn(PSP_CODE);
        PspChannelPaymentTypesResource result =
                assertDoesNotThrow(() -> sut.updatePSPChannel(TAX_CODE, CHANNEL_CODE, new PspChannelPaymentTypes()));

        assertNotNull(result);
    }

    @Test
    void deletePSPChannel() {
        assertDoesNotThrow(() -> sut.dissociatePSPFromChannel(TAX_CODE, CHANNEL_CODE));
    }

    @Test
    void getFirstValidChannelCodeWithV2True() {
        String channelCodeV2 = "channel-code-v2";
        when(wrapperServiceMock.getFirstValidChannelCodeV2(anyString()))
                .thenReturn(channelCodeV2);
        ChannelCodeResource result =
                assertDoesNotThrow(() -> sut.getFirstValidChannelCode(TAX_CODE, true));

        assertNotNull(result);
        assertEquals(channelCodeV2, result.getChannelCode());
        verify(wrapperServiceMock).getFirstValidChannelCodeV2(anyString());
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
        when(apiConfigClientMock.getChannels(anyInt(), anyInt(), eq(null), anyString(), anyString()))
                .thenReturn(channels);
        ChannelCodeResource result =
                assertDoesNotThrow(() -> sut.getFirstValidChannelCode(TAX_CODE, false));

        assertNotNull(result);
        assertNotEquals(channelCode, result.getChannelCode());
        verify(wrapperServiceMock, never()).getFirstValidChannelCodeV2(anyString());
        verify(apiConfigClientMock).getChannels(anyInt(), anyInt(), eq(null), anyString(), anyString());
    }

    private PaymentServiceProviderDetails getPaymentServiceProviderDetails() {
        return PaymentServiceProviderDetails.builder()
                .abi("abi")
                .agidPsp(true)
                .bic("bic")
                .build();
    }
}
