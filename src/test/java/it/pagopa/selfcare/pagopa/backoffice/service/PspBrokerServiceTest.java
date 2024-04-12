package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerPspDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokersPspResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResourceList;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProvidersResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokerPsp;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokersPsp;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentModel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProvider;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviders;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MappingsConfiguration.class, PspBrokerService.class})
class PspBrokerServiceTest {

    private static final String BROKER_PSP_CODE = "brokerPspCode";
    private static final String DESCRIPTION = "description";

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    private PspBrokerService sut;

    @Test
    void getBrokersForPSP() {
        BrokerPsp brokerPsp = buildBrokerPsp();
        BrokersPsp brokersPsp = BrokersPsp.builder()
                .brokerPspList(Collections.singletonList(brokerPsp))
                .pageInfo(buildPageInfo())
                .build();

        when(apiConfigClient
                .getBrokersPsp(10, 0, "filterByCode", "filterByeName", "orderBy", "sorting")
        ).thenReturn(brokersPsp);

        BrokersPspResource result =
                assertDoesNotThrow(() ->
                        sut.getBrokersForPSP(
                                10,
                                0,
                                "filterByCode",
                                "filterByeName",
                                "orderBy",
                                "sorting"));

        assertNotNull(result);
        assertNotNull(result.getBrokerPspList());
        assertEquals(1, result.getBrokerPspList().size());
        assertNotNull(result.getPageInfo());
        assertEquals(brokerPsp.getBrokerPspCode(), result.getBrokerPspList().get(0).getBrokerPspCode());
        assertEquals(brokerPsp.getDescription(), result.getBrokerPspList().get(0).getDescription());
        assertEquals(brokerPsp.getEnabled(), result.getBrokerPspList().get(0).getEnabled());

        assertEquals(brokersPsp.getPageInfo().getPage(), result.getPageInfo().getPage());
        assertEquals(brokersPsp.getPageInfo().getTotalPages(), result.getPageInfo().getTotalPages());
        assertEquals(brokersPsp.getPageInfo().getTotalItems(), result.getPageInfo().getTotalItems());
        assertEquals(brokersPsp.getPageInfo().getLimit(), result.getPageInfo().getLimit());
        assertEquals(brokersPsp.getPageInfo().getItemsFound(), result.getPageInfo().getItemsFound());
    }

    @Test
    void getBrokerForPsp() {
        BrokerPspDetails brokerPspDetails = buildBrokerPspDetails();

        when(apiConfigClient.getBrokerPsp(BROKER_PSP_CODE)).thenReturn(brokerPspDetails);

        BrokerPspDetailsResource result = assertDoesNotThrow(() -> sut.getBrokerForPsp(BROKER_PSP_CODE));

        assertNotNull(result);
        assertEquals(brokerPspDetails.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(brokerPspDetails.getDescription(), result.getDescription());
        assertEquals(brokerPspDetails.getEnabled(), result.getEnabled());
        assertEquals(brokerPspDetails.getExtendedFaultBean(), result.getExtendedFaultBean());
    }

    @Test
    void createBrokerForPSP() {
        BrokerPspDetailsDto brokerPspDetailsDto = buildBrokerPspDetailsDto();

        when(apiConfigClient.createBrokerPsp(any(BrokerPspDetails.class))).thenReturn(buildBrokerPspDetails());

        BrokerPspDetailsResource result = assertDoesNotThrow(() -> sut.createBrokerForPSP(brokerPspDetailsDto));

        assertNotNull(result);
        assertEquals(brokerPspDetailsDto.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(brokerPspDetailsDto.getDescription(), result.getDescription());
        assertEquals(brokerPspDetailsDto.getEnabled(), result.getEnabled());
        assertEquals(brokerPspDetailsDto.getExtendedFaultBean(), result.getExtendedFaultBean());
    }

    @Test
    void updateBrokerPSP() {
        BrokerPspDetailsDto brokerPspDetailsDto = buildBrokerPspDetailsDto();

        when(apiConfigClient.updateBrokerPSP(eq(BROKER_PSP_CODE), any(BrokerPspDetails.class))).thenReturn(buildBrokerPspDetails());

        BrokerPspDetailsResource result = assertDoesNotThrow(() -> sut.updateBrokerPSP(BROKER_PSP_CODE, brokerPspDetailsDto));

        assertNotNull(result);
        assertEquals(brokerPspDetailsDto.getBrokerPspCode(), result.getBrokerPspCode());
        assertEquals(brokerPspDetailsDto.getDescription(), result.getDescription());
        assertEquals(brokerPspDetailsDto.getEnabled(), result.getEnabled());
        assertEquals(brokerPspDetailsDto.getExtendedFaultBean(), result.getExtendedFaultBean());
    }

    @Test
    void getChannelByBroker() {
        ChannelDetailsList channelDetailsList = new ChannelDetailsList();
        ChannelDetails channelDetails = buildChannelDetails();
        channelDetailsList.setChannelDetailsList(Collections.singletonList(channelDetails));
        channelDetailsList.setPageInfo(buildPageInfo());

        when(apiConfigSelfcareIntegrationClient
                .getChannelDetailsListByBroker(BROKER_PSP_CODE, "channelId", 10, 0)
        ).thenReturn(channelDetailsList);

        ChannelDetailsResourceList result = assertDoesNotThrow(() ->
                sut.getChannelByBroker(BROKER_PSP_CODE, "channelId", 10, 0));

        assertNotNull(result);
        assertNotNull(result.getPageInfo());
        assertEquals(1, result.getChannelDetailsResources().size());
        ChannelDetailsResource detailsResource = result.getChannelDetailsResources().get(0);

        assertEquals(channelDetails.getPassword(), detailsResource.getPassword());
        assertEquals(channelDetails.getNewPassword(), detailsResource.getNewPassword());
        assertEquals(channelDetails.getProtocol(), detailsResource.getProtocol());
        assertEquals(channelDetails.getIp(), detailsResource.getIp());
        assertEquals(channelDetails.getPort(), detailsResource.getPort());
        assertEquals(channelDetails.getService(), detailsResource.getService());
        assertEquals(channelDetails.getBrokerPspCode(), detailsResource.getBrokerPspCode());
        assertEquals(channelDetails.getProxyEnabled(), detailsResource.getProxyEnabled());
        assertEquals(channelDetails.getProxyHost(), detailsResource.getProxyHost());
        assertEquals(channelDetails.getProxyPort(), detailsResource.getProxyPort());
        assertEquals(channelDetails.getProxyUsername(), detailsResource.getProxyUsername());
        assertEquals(channelDetails.getProxyPassword(), detailsResource.getProxyPassword());
        assertEquals(channelDetails.getTargetHost(), detailsResource.getTargetHost());
        assertEquals(channelDetails.getTargetPort(), detailsResource.getTargetPort());
        assertEquals(channelDetails.getTargetPath(), detailsResource.getTargetPath());
        assertEquals(channelDetails.getThreadNumber(), detailsResource.getThreadNumber());
        assertEquals(channelDetails.getTimeoutA(), detailsResource.getTimeoutA());
        assertEquals(channelDetails.getTimeoutB(), detailsResource.getTimeoutB());
        assertEquals(channelDetails.getTimeoutC(), detailsResource.getTimeoutC());
        assertEquals(channelDetails.getNmpService(), detailsResource.getNmpService());
        assertEquals(channelDetails.getNewFaultCode(), detailsResource.getNewFaultCode());
        assertEquals(channelDetails.getRedirectIp(), detailsResource.getRedirectIp());
        assertEquals(channelDetails.getRedirectPath(), detailsResource.getRedirectPath());
        assertEquals(channelDetails.getRedirectPort(), detailsResource.getRedirectPort());
        assertEquals(channelDetails.getRedirectQueryString(), detailsResource.getRedirectQueryString());
        assertEquals(channelDetails.getRedirectProtocol(), detailsResource.getRedirectProtocol());
        assertEquals(channelDetails.getPaymentModel(), detailsResource.getPaymentModel());
        assertEquals(channelDetails.getServPlugin(), detailsResource.getServPlugin());
        assertEquals(channelDetails.getRtPush(), detailsResource.getRtPush());
        assertEquals(channelDetails.getOnUs(), detailsResource.getOnUs());
        assertEquals(channelDetails.getCardChart(), detailsResource.getCardChart());
        assertEquals(channelDetails.getRecovery(), detailsResource.getRecovery());
        assertEquals(channelDetails.getDigitalStampBrand(), detailsResource.getDigitalStampBrand());
        assertEquals(channelDetails.getFlagIo(), detailsResource.getFlagIo());
        assertEquals(channelDetails.getAgid(), detailsResource.getAgid());
        assertEquals(channelDetails.getBrokerDescription(), detailsResource.getBrokerDescription());
        assertEquals(channelDetails.getEnabled(), detailsResource.getEnabled());
        assertEquals(channelDetails.getChannelCode(), detailsResource.getChannelCode());
        assertEquals(channelDetails.getTargetHostNmp(), detailsResource.getTargetHostNmp());
        assertEquals(channelDetails.getTargetPortNmp(), detailsResource.getTargetPortNmp());
        assertEquals(channelDetails.getTargetPathNmp(), detailsResource.getTargetPathNmp());
    }

    @Test
    void getPSPAssociatedToBroker() {
        PaymentServiceProvider psp = buildPsp();
        PaymentServiceProviders paymentServiceProviders = PaymentServiceProviders.builder()
                .paymentServiceProviderList(Collections.singletonList(psp))
                .pageInfo(buildPageInfo())
                .build();

        when(apiConfigClient.getPspBrokerPsp(10, 0, BROKER_PSP_CODE)).thenReturn(paymentServiceProviders);

        PaymentServiceProvidersResource result = assertDoesNotThrow(() -> sut.getPSPAssociatedToBroker(BROKER_PSP_CODE, 10, 0));

        assertNotNull(result);
        assertNotNull(result.getPaymentServiceProviderList());
        assertEquals(1, result.getPaymentServiceProviderList().size());
        assertNotNull(result.getPageInfo());
        assertEquals(psp.getPspCode(), result.getPaymentServiceProviderList().get(0).getPspCode());
        assertEquals(psp.getBusinessName(), result.getPaymentServiceProviderList().get(0).getBusinessName());
        assertEquals(psp.getEnabled(), result.getPaymentServiceProviderList().get(0).getEnabled());

        assertEquals(paymentServiceProviders.getPageInfo().getPage(), result.getPageInfo().getPage());
        assertEquals(paymentServiceProviders.getPageInfo().getTotalPages(), result.getPageInfo().getTotalPages());
        assertEquals(paymentServiceProviders.getPageInfo().getTotalItems(), result.getPageInfo().getTotalItems());
        assertEquals(paymentServiceProviders.getPageInfo().getLimit(), result.getPageInfo().getLimit());
        assertEquals(paymentServiceProviders.getPageInfo().getItemsFound(), result.getPageInfo().getItemsFound());
    }

    @Test
    void deletePspBroker() {
        assertDoesNotThrow(() -> sut.deletePspBroker(BROKER_PSP_CODE));
    }

    private PaymentServiceProvider buildPsp() {
        return PaymentServiceProvider.builder()
                .pspCode("pspCode")
                .enabled(true)
                .businessName("businessName")
                .build();
    }

    private BrokerPspDetailsDto buildBrokerPspDetailsDto() {
        return BrokerPspDetailsDto.builder()
                .brokerPspCode(BROKER_PSP_CODE)
                .description(DESCRIPTION)
                .enabled(true)
                .extendedFaultBean(true)
                .build();
    }

    private BrokerPspDetails buildBrokerPspDetails() {
        BrokerPspDetails brokersPsp = new BrokerPspDetails();
        brokersPsp.setBrokerPspCode(BROKER_PSP_CODE);
        brokersPsp.setDescription(DESCRIPTION);
        brokersPsp.setEnabled(true);
        brokersPsp.setExtendedFaultBean(true);
        return brokersPsp;
    }

    private BrokerPsp buildBrokerPsp() {
        return BrokerPsp.builder()
                .brokerPspCode(BROKER_PSP_CODE)
                .description(DESCRIPTION)
                .enabled(true)
                .build();
    }

    private PageInfo buildPageInfo() {
        return PageInfo.builder()
                .totalPages(1)
                .page(0)
                .limit(10)
                .totalItems(1L)
                .itemsFound(1)
                .build();
    }

    private ChannelDetails buildChannelDetails() {
        ChannelDetails channelDetails = new ChannelDetails();
        channelDetails.setPassword("password");
        channelDetails.setNewPassword("newPassword");
        channelDetails.setProtocol(Protocol.HTTPS);
        channelDetails.setIp("ip");
        channelDetails.setPort(8888L);
        channelDetails.setService("service");
        channelDetails.setBrokerPspCode(BROKER_PSP_CODE);
        channelDetails.setProxyEnabled(true);
        channelDetails.setProxyHost("proxyHost");
        channelDetails.setProxyPort(4444L);
        channelDetails.setProxyUsername("proxyUsername");
        channelDetails.setProxyPassword("proxyPassword");
        channelDetails.setTargetHost("targetHost");
        channelDetails.setTargetPort(4445L);
        channelDetails.setTargetPath("targetPath");
        channelDetails.setThreadNumber(2L);
        channelDetails.setTimeoutA(1000L);
        channelDetails.setTimeoutB(500L);
        channelDetails.setTimeoutC(250L);
        channelDetails.setNmpService("nmpService");
        channelDetails.setNewFaultCode(false);
        channelDetails.setRedirectIp("redirectIp");
        channelDetails.setRedirectPath("redirectPath");
        channelDetails.setRedirectPort(9000L);
        channelDetails.setRedirectQueryString("redirectQueryString");
        channelDetails.setRedirectProtocol(Protocol.HTTP);
        channelDetails.setPaymentModel(PaymentModel.IMMEDIATE);
        channelDetails.setServPlugin("aervPlugin");
        channelDetails.setRtPush(false);
        channelDetails.setOnUs(true);
        channelDetails.setCardChart(false);
        channelDetails.setRecovery(true);
        channelDetails.setDigitalStampBrand(true);
        channelDetails.setFlagIo(true);
        channelDetails.setAgid(true);
        channelDetails.setBrokerDescription("brokerDescription");
        channelDetails.setEnabled(true);
        channelDetails.setChannelCode("channelCode");
        channelDetails.setPrimitiveVersion(1);
        channelDetails.setPaymentTypeList(Collections.singletonList("paymentType"));
        channelDetails.setTargetHostNmp("targetHostNmp");
        channelDetails.setTargetPortNmp("targetPortNmp");
        channelDetails.setTargetPathNmp("targetPathNmp");
        return channelDetails;
    }
}