package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {ChannelController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        ChannelController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class ChannelControllerTest {

    private static final String BASE_URL = "/channels";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @MockBean
    private ApiConfigConnector apiConfigConnectorMock;


    @Test
    void getChannels() throws Exception {
        //given

        Integer limit = 1;
        Integer page = 1;
        String code = "code";
        String sort = "DESC";
        String xRequestId = "1";

        Channel channel = mockInstance(new Channel());
        Channels channels = mockInstance(new Channels(), "setchannelList");
        channels.setChannelList(List.of(channel));
        PageInfo pageInfo = mockInstance(new PageInfo());
        channels.setPageInfo(pageInfo);

        when(apiConfigServiceMock.getChannels(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(channels);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .queryParam("code", code)
                        .queryParam("sort", sort)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_info", notNullValue()))
                .andExpect(jsonPath("$.channels", notNullValue()))
                .andExpect(jsonPath("$.channels", not(empty())))
                .andExpect(jsonPath("$.channels[0].broker_description", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getChannels(eq(limit), eq(page), eq(code), eq(sort), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getChannelDetails() throws Exception {
        //given
        String channelcode = "channelcode";
        String xRequestId = "1";

        ChannelDetails channelDetails = mockInstance(new ChannelDetails());
        PspChannelPaymentTypes paymentTypes = mockInstance(new PspChannelPaymentTypes());
        paymentTypes.setPaymentTypeList(List.of("paymentType"));

        when(apiConfigServiceMock.getChannelDetails(anyString(), anyString()))
                .thenReturn(channelDetails);

        when(apiConfigServiceMock.getChannelPaymentTypes(anyString(), anyString()))
                .thenReturn(paymentTypes);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/details/{channelcode}", channelcode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.password", is(channelDetails.getPassword())))
                .andExpect(jsonPath("$.new_password", is(channelDetails.getNewPassword())))
                .andExpect(jsonPath("$.protocol", is(channelDetails.getProtocol().name())))
                .andExpect(jsonPath("$.ip", is(channelDetails.getIp())))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.service", is(channelDetails.getService())))
                .andExpect(jsonPath("$.broker_psp_code", is(channelDetails.getBrokerPspCode())))
                .andExpect(jsonPath("$.proxy_enabled", is(channelDetails.getProxyEnabled())))
                .andExpect(jsonPath("$.proxy_host", is(channelDetails.getProxyHost())))
                .andExpect(jsonPath("$.proxy_port", notNullValue()))
                .andExpect(jsonPath("$.proxy_username", is(channelDetails.getProxyUsername())))
                .andExpect(jsonPath("$.target_host", is(channelDetails.getTargetHost())))
                .andExpect(jsonPath("$.target_port", notNullValue()))
                .andExpect(jsonPath("$.target_path", is(channelDetails.getTargetPath())))
                .andExpect(jsonPath("$.thread_number", notNullValue()))
                .andExpect(jsonPath("$.timeout_a", notNullValue()))
                .andExpect(jsonPath("$.timeout_b", notNullValue()))
                .andExpect(jsonPath("$.timeout_c", notNullValue()))
                .andExpect(jsonPath("$.npm_service", is(channelDetails.getNpmService())))
                .andExpect(jsonPath("$.new_fault_code", is(channelDetails.getNewFaultCode())))
                .andExpect(jsonPath("$.redirect_ip", is(channelDetails.getRedirectIp())))
                .andExpect(jsonPath("$.redirect_path", is(channelDetails.getRedirectPath())));

        //then
        verify(apiConfigServiceMock, times(1))
                .getChannelDetails(eq(channelcode), anyString());
        verify(apiConfigServiceMock, times(1))
                .getChannelPaymentTypes(eq(channelcode), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);

    }

    @Test
    void createChannel(@Value("classpath:stubs/channelDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";
        String channelCode = "setChannelCode";
        PspChannelPaymentTypes pspChannelPaymentTypes = mockInstance(new PspChannelPaymentTypes());
        InputStream is = dto.getInputStream();
        ChannelDetails channelDetails = objectMapper.readValue(is, ChannelDetails.class);

        when(apiConfigServiceMock.createChannel(any(), anyString()))
                .thenReturn(channelDetails);

        when(apiConfigServiceMock.createChannelPaymentType(any(), anyString(), anyString()))
                .thenReturn(pspChannelPaymentTypes);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.password", is(channelDetails.getPassword())))
                .andExpect(jsonPath("$.new_password", is(channelDetails.getNewPassword())))
                .andExpect(jsonPath("$.protocol", is(channelDetails.getProtocol().name())))
                .andExpect(jsonPath("$.ip", is(channelDetails.getIp())))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.service", is(channelDetails.getService())))
                .andExpect(jsonPath("$.broker_psp_code", is(channelDetails.getBrokerPspCode())))
                .andExpect(jsonPath("$.proxy_enabled", is(channelDetails.getProxyEnabled())))
                .andExpect(jsonPath("$.proxy_host", is(channelDetails.getProxyHost())))
                .andExpect(jsonPath("$.proxy_port", notNullValue()))
                .andExpect(jsonPath("$.proxy_username", is(channelDetails.getProxyUsername())))
                .andExpect(jsonPath("$.target_host", is(channelDetails.getTargetHost())))
                .andExpect(jsonPath("$.target_port", notNullValue()))
                .andExpect(jsonPath("$.target_path", is(channelDetails.getTargetPath())))
                .andExpect(jsonPath("$.thread_number", notNullValue()))
                .andExpect(jsonPath("$.timeout_a", notNullValue()))
                .andExpect(jsonPath("$.timeout_b", notNullValue()))
                .andExpect(jsonPath("$.timeout_c", notNullValue()))
                .andExpect(jsonPath("$.npm_service", is(channelDetails.getNpmService())))
                .andExpect(jsonPath("$.new_fault_code", is(channelDetails.getNewFaultCode())))
                .andExpect(jsonPath("$.redirect_ip", is(channelDetails.getRedirectIp())))
                .andExpect(jsonPath("$.redirect_path", is(channelDetails.getRedirectPath())));

        //then
        verify(apiConfigServiceMock, times(1))
                .createChannel(eq(channelDetails), anyString());

        verify(apiConfigServiceMock, times(1))
                .createChannelPaymentType(eq(pspChannelPaymentTypes), eq(channelCode), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void updateChannel(@Value("classpath:stubs/channelDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";
        String channelCode = "setChannelCode";
        InputStream is = dto.getInputStream();
        ChannelDetails channelDetails = objectMapper.readValue(is, ChannelDetails.class);

        when(apiConfigServiceMock.updateChannel(any(), anyString(), anyString()))
                .thenReturn(channelDetails);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/{channelcode}", channelCode)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-Request-Id", String.valueOf(xRequestId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.password", is(channelDetails.getPassword())))
                .andExpect(jsonPath("$.new_password", is(channelDetails.getNewPassword())))
                .andExpect(jsonPath("$.protocol", is(channelDetails.getProtocol().name())))
                .andExpect(jsonPath("$.ip", is(channelDetails.getIp())))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.service", is(channelDetails.getService())))
                .andExpect(jsonPath("$.broker_psp_code", is(channelDetails.getBrokerPspCode())))
                .andExpect(jsonPath("$.proxy_enabled", is(channelDetails.getProxyEnabled())))
                .andExpect(jsonPath("$.proxy_host", is(channelDetails.getProxyHost())))
                .andExpect(jsonPath("$.proxy_port", notNullValue()))
                .andExpect(jsonPath("$.proxy_username", is(channelDetails.getProxyUsername())))
                .andExpect(jsonPath("$.target_host", is(channelDetails.getTargetHost())))
                .andExpect(jsonPath("$.target_port", notNullValue()))
                .andExpect(jsonPath("$.target_path", is(channelDetails.getTargetPath())))
                .andExpect(jsonPath("$.thread_number", notNullValue()))
                .andExpect(jsonPath("$.timeout_a", notNullValue()))
                .andExpect(jsonPath("$.timeout_b", notNullValue()))
                .andExpect(jsonPath("$.timeout_c", notNullValue()))
                .andExpect(jsonPath("$.npm_service", is(channelDetails.getNpmService())))
                .andExpect(jsonPath("$.new_fault_code", is(channelDetails.getNewFaultCode())))
                .andExpect(jsonPath("$.redirect_ip", is(channelDetails.getRedirectIp())))
                .andExpect(jsonPath("$.redirect_path", is(channelDetails.getRedirectPath())));

        //then
        verify(apiConfigServiceMock, times(1))
                .updateChannel(any(), anyString(), anyString());


        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void createChannelPaymentType() throws Exception {
        //given
        String xRequestId = "1";
        String channelCode = "channelCode";
        PspChannelPaymentTypes pspChannelPaymentTypes = mockInstance(new PspChannelPaymentTypes());
        pspChannelPaymentTypes.setPaymentTypeList(List.of("paymentType"));

        PspChannelPaymentTypes dto = mockInstance(new PspChannelPaymentTypes());
        pspChannelPaymentTypes.setPaymentTypeList(List.of("paymentType"));

        when(apiConfigServiceMock.createChannelPaymentType(any(), anyString(), anyString()))
                .thenReturn(pspChannelPaymentTypes);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/{channelcode}/paymenttypes", channelCode)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.payment_types", notNullValue()))
                .andExpect(jsonPath("$.payment_types[0]", notNullValue()));

        //then
        verify(apiConfigServiceMock, times(1))
                .createChannelPaymentType(any(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getPspChannels() throws Exception {
        //given

        String pspCode = "pspCode";
        String xRequestId = "1";

        PspChannel pspChannel = mockInstance(new PspChannel(), "setPaymentTypeList");
        pspChannel.setPaymentTypeList(List.of("paymentType"));

        PspChannels pspChannels = mockInstance(new PspChannels(), "setchannelList");
        pspChannels.setChannelsList(List.of(pspChannel));

        when(apiConfigServiceMock.getPspChannels(anyString(), anyString()))
                .thenReturn(pspChannels);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{pdpcode}", pspCode)
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channels[*].enabled", everyItem(notNullValue())))
                .andExpect(jsonPath("$.channels[*].channel_code", everyItem(notNullValue())))
                .andExpect(jsonPath("$.channels[*].payment_types", everyItem(notNullValue())));
        //then
        verify(apiConfigServiceMock, times(1))
                .getPspChannels(eq(pspCode), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getPaymentTypes() throws Exception {
        //given

        String pspCode = "pspCode";
        String xRequestId = "1";

        PaymentTypes paymentTypes = mockInstance(new PaymentTypes(), "setPaymentTypeList");
        PaymentType paymentType = mockInstance(new PaymentType());
        paymentTypes.setPaymentTypeList(List.of(paymentType));


        when(apiConfigServiceMock.getPaymentTypes(anyString()))
                .thenReturn(paymentTypes);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/configuration/paymenttypes")
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_types[*].description", everyItem(notNullValue())))
                .andExpect(jsonPath("$.payment_types[*].payment_type", everyItem(notNullValue())));

        //then
        verify(apiConfigServiceMock, times(1))
                .getPaymentTypes(anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getChannelPaymentTypes() throws Exception {
        //given

        String channelCode = "channelCode";
        String xRequestId = "1";

        PspChannelPaymentTypes pspChannelPaymentTypes = mockInstance(new PspChannelPaymentTypes(), "setPaymentTypeList");
        pspChannelPaymentTypes.setPaymentTypeList(List.of("paymentType"));

        when(apiConfigServiceMock.getChannelPaymentTypes(anyString(), anyString()))
                .thenReturn(pspChannelPaymentTypes);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/paymenttypes/{channelcode}", channelCode)
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_types[*]", everyItem(notNullValue())));

        //then
        verify(apiConfigServiceMock, times(1))
                .getChannelPaymentTypes(anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void deleteChannelPaymentType() throws Exception {
        //given

        String channelCode = "channelCode";
        String xRequestId = "1";
        String paymentTypeCode = "paymenttypecode";

        doNothing().when(apiConfigServiceMock).deleteChannelPaymentType(anyString(), anyString(), anyString());

        //when
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/{channelcode}/{paymenttypecode}", channelCode, paymentTypeCode)
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        //then
        verify(apiConfigServiceMock, times(1))
                .deleteChannelPaymentType(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void deletePaymentServiceProvidersChannels() throws Exception {
        //given
        String channelCode = "channelCode";
        String xRequestId = "1";
        String pspCode = "pspCode";

        doNothing().when(apiConfigServiceMock).deletePaymentServiceProvidersChannels(anyString(), anyString(), anyString());

        //when
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/psp/{channelcode}/{pspcode}", channelCode, pspCode)
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        //then
        verify(apiConfigServiceMock, times(1))
                .deletePaymentServiceProvidersChannels(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void deleteChannel() throws Exception {
        //given
        String channelCode = "channelCode";
        String xRequestId = "1";

        //when
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/{channelcode}", channelCode)
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        //then
        verify(apiConfigServiceMock, times(1))
                .deleteChannel(anyString(), anyString());
    }

    @Test
    void getPspBrokerPsp() throws Exception {
        //given

        String brokerPspCode = "brokerpspcode";
        String xRequestId = "1";
        String limit = "1";
        String page = "1";

        PaymentServiceProviders paymentServiceProviders = mockInstance(new PaymentServiceProviders(), "setPaymentServiceProviderList");
        PaymentServiceProvider paymentServiceProvider = mockInstance(new PaymentServiceProvider());
        paymentServiceProviders.setPaymentServiceProviderList(List.of(paymentServiceProvider));


        when(apiConfigServiceMock.getPspBrokerPsp(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(paymentServiceProviders);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{brokerpspcode}/paymentserviceproviders", brokerPspCode)
                        .param("page", "1")
                        .param("limit", "1")
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_info", notNullValue()))
                .andExpect(jsonPath("$.payment_service_providers[*].payment_type", everyItem(notNullValue())));

        //then
        verify(apiConfigServiceMock, times(1))
                .getPspBrokerPsp(anyInt(), anyInt(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void updatePaymentServiceProvidersChannels(@Value("classpath:stubs/pspChannelPaymentTypesDto.json") Resource dto) throws Exception {
        //given
        final String channelCode = "channelCode";
        final String pspCode = "pspCode";
        final String xRequestId = "1";


        InputStream is = dto.getInputStream();
        PspChannelPaymentTypes pspChannelPaymentTypes = objectMapper.readValue(is, PspChannelPaymentTypes.class);
        when(apiConfigServiceMock.updatePaymentServiceProvidersChannels(anyString(), anyString(), any(), anyString()))
                .thenReturn(pspChannelPaymentTypes);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/psp/{channelcode}/{pspcode}", channelCode, pspCode)
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(dto.getInputStream().readAllBytes()))


                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_types[*]", everyItem(notNullValue())));
        //then
        verify(apiConfigServiceMock, times(1))
                .updatePaymentServiceProvidersChannels(anyString(), anyString(), any(), anyString());

    }

    @Test
    void getChannelsCSV() throws Exception {

        File file = File.createTempFile("channels", ".csv");
        FileWriter writer = new FileWriter(file);
        writer.write("id,name\n1,channel1\n2,channel2\n");
        writer.close();

        String xRequestId = "1";

        Resource resource = mockInstance(new FileSystemResource(file));
        when(apiConfigServiceMock.getChannelsCSV(anyString()))
                .thenReturn(resource);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/csv")
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk());

        verify(apiConfigServiceMock, times(1))
                .getChannelsCSV(anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);

    }

    @Test
    void getChannelPaymentServiceProviders() throws Exception {
        final String channelCode = "channelCode";
        final String xRequestId = "1";


        ChannelPspList pspChannelPaymentTypes = mockInstance(new ChannelPspList());
        ChannelPsp channelPsp = mockInstance(new ChannelPsp());
        pspChannelPaymentTypes.setPsp(List.of(channelPsp));


        when(apiConfigServiceMock.getChannelPaymentServiceProviders(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pspChannelPaymentTypes);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{channelcode}/psp", channelCode)
                        .header("X-Request-Id", String.valueOf(xRequestId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_service_providers[*]", everyItem(notNullValue())));
        //then
        verify(apiConfigServiceMock, times(1))
                .getChannelPaymentServiceProviders(anyInt(), anyInt(), anyString(), anyString());


    }


    @Test
    void createBrokerPsp(@Value("classpath:stubs/brokerPspDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";

        PspChannelPaymentTypes pspChannelPaymentTypes = mockInstance(new PspChannelPaymentTypes());
        InputStream is = dto.getInputStream();
        BrokerPspDetails brokerPspDetails = objectMapper.readValue(is, BrokerPspDetails.class);

        when(apiConfigServiceMock.createBrokerPsp(any(), anyString()))
                .thenReturn(brokerPspDetails);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/brokerspsp")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))

                .andExpect(jsonPath("$.broker_psp_code", is(brokerPspDetails.getBrokerPspCode())))
                .andExpect(jsonPath("$.description", is(brokerPspDetails.getDescription())))
                .andExpect(jsonPath("$.enabled", is(brokerPspDetails.getEnabled())))
                .andExpect(jsonPath("$.extended_fault_bean", is(brokerPspDetails.getExtendedFaultBean())));
        //then
        verify(apiConfigServiceMock, times(1))
                .createBrokerPsp(any(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);

    }

    @Test
    void createPaymentServiceProvider(@Value("classpath:stubs/paymentServiceProviderDetailsDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";
        InputStream is = dto.getInputStream();

        PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto = objectMapper.readValue(is, PaymentServiceProviderDetailsDto.class);

        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);

        when(apiConfigServiceMock.createPaymentServiceProvider(any(), anyString()))
                .thenReturn(paymentServiceProviderDetails);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/psp")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))

                .andExpect(jsonPath("$.abi", is(paymentServiceProviderDetailsDto.getAbi())))
                .andExpect(jsonPath("$.bic", is(paymentServiceProviderDetailsDto.getBic())))
                .andExpect(jsonPath("$.stamp", is(paymentServiceProviderDetailsDto.getStamp())))
                .andExpect(jsonPath("$.agid_psp", is(paymentServiceProviderDetailsDto.getAgidPsp())))
                .andExpect(jsonPath("$.vat_number", is(paymentServiceProviderDetailsDto.getVatNumber())))
                .andExpect(jsonPath("$.transfer", is(paymentServiceProviderDetailsDto.getTransfer())))
                .andExpect(jsonPath("$.psp_code", is(paymentServiceProviderDetailsDto.getPspCode())))
                .andExpect(jsonPath("$.business_name", is(paymentServiceProviderDetailsDto.getBusinessName())))
                .andExpect(jsonPath("$.enabled", is(paymentServiceProviderDetailsDto.getEnabled())));
        //then
        verify(apiConfigServiceMock, times(1))
                .createPaymentServiceProvider(any(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);

    }

    @Test
    void createPSPDirect(@Value("classpath:stubs/paymentServiceProviderDetailsDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";
        InputStream is = dto.getInputStream();

        PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto = objectMapper.readValue(is, PaymentServiceProviderDetailsDto.class);

        Map<String, Object> res = ChannelMapper.fromPaymentServiceProviderDetailsDtoToMap(paymentServiceProviderDetailsDto);

        BrokerPspDetails brokerPspDetails = (BrokerPspDetails) res.get("broker");
        PaymentServiceProviderDetails paymentServiceProviderDetails = (PaymentServiceProviderDetails) res.get("psp");


        when(apiConfigServiceMock.createPaymentServiceProvider(any(), anyString()))
                .thenReturn(paymentServiceProviderDetails);

        when(apiConfigServiceMock.createBrokerPsp(any(), anyString()))
                .thenReturn(brokerPspDetails);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/pspdirect")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))

                .andExpect(jsonPath("$.abi", is(paymentServiceProviderDetailsDto.getAbi())))
                .andExpect(jsonPath("$.bic", is(paymentServiceProviderDetailsDto.getBic())))
                .andExpect(jsonPath("$.stamp", is(paymentServiceProviderDetailsDto.getStamp())))
                .andExpect(jsonPath("$.agid_psp", is(paymentServiceProviderDetailsDto.getAgidPsp())))
                .andExpect(jsonPath("$.vat_number", is(paymentServiceProviderDetailsDto.getVatNumber())))
                .andExpect(jsonPath("$.transfer", is(paymentServiceProviderDetailsDto.getTransfer())))
                .andExpect(jsonPath("$.psp_code", is(paymentServiceProviderDetailsDto.getPspCode())))
                .andExpect(jsonPath("$.business_name", is(paymentServiceProviderDetailsDto.getBusinessName())))
                .andExpect(jsonPath("$.enabled", is(paymentServiceProviderDetailsDto.getEnabled())));
        //then
        verify(apiConfigServiceMock, times(1))
                .createPaymentServiceProvider(any(), anyString());
        verify(apiConfigServiceMock, times(1))
                .createBrokerPsp(any(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);

    }

    @Test
    void getChannelCode() throws Exception {

        String pspCode = "pspCode";
        String channelCode = "channelCode";

        when(apiConfigServiceMock.generateChannelCode(any(), anyString()))
                .thenReturn(channelCode);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{pspcode}/generate",pspCode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))


                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", is(channelCode)));

        verify(apiConfigServiceMock, times(1))
                .generateChannelCode(any(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }


    @Test
    void getPSPDetails() throws Exception {
        String pspCode = "pspCode";
        PaymentServiceProviderDetails paymentServiceProviderDetails = mockInstance(new PaymentServiceProviderDetails());

        when(apiConfigServiceMock.getPSPDetails(anyString(), anyString()))
                .thenReturn(paymentServiceProviderDetails);

        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/psp/{pspcode}",pspCode)
                        .contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().is2xxSuccessful())

                .andExpect(jsonPath("$.abi", is(paymentServiceProviderDetails.getAbi())))
                .andExpect(jsonPath("$.bic", is(paymentServiceProviderDetails.getBic())))
                .andExpect(jsonPath("$.stamp", is(paymentServiceProviderDetails.getStamp())))
                .andExpect(jsonPath("$.agid_psp", is(paymentServiceProviderDetails.getAgidPsp())))
                .andExpect(jsonPath("$.vat_number", is(paymentServiceProviderDetails.getVatNumber())))
                .andExpect(jsonPath("$.transfer", is(paymentServiceProviderDetails.getTransfer())))
                .andExpect(jsonPath("$.psp_code", is(paymentServiceProviderDetails.getPspCode())))
                .andExpect(jsonPath("$.business_name", is(paymentServiceProviderDetails.getBusinessName())))
                .andExpect(jsonPath("$.enabled", is(paymentServiceProviderDetails.getEnabled())));


        verify(apiConfigServiceMock, times(1))
                .getPSPDetails(any(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }
}
