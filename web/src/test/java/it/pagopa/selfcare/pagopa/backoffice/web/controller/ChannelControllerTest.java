package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsResource;
import org.junit.jupiter.api.Test;
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

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
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

        when(apiConfigServiceMock.getChannels(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(channels);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .queryParam("code", code)
                        .queryParam("sort", sort)
                        .header("X-Request-Id", String.valueOf(xRequestId))


                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.page_info", notNullValue()))
                .andExpect(jsonPath("$.channels", notNullValue()))
                .andExpect(jsonPath("$.channels", not(empty())))
                .andExpect(jsonPath("$.channels[0].broker_description", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigServiceMock);
    }


    @Test
    void createChannel(@Value("classpath:stubs/channelDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";
        String channelCode = "setChannelCode";
        PspChannelPaymentTypes pspChannelPaymentTypes = mockInstance(new PspChannelPaymentTypes());
        ChannelDetails channelDetails = mockInstance(new ChannelDetails());
        channelDetails.setPassword("password");
        channelDetails.setNewPassword("newPassword");
        channelDetails.setProtocol(Protocol.HTTP);
        channelDetails.setIp("127.0.0.1");
        channelDetails.setPort(Long.parseLong("8080"));
        channelDetails.setService("service");
        channelDetails.setBrokerPspCode("psp");
        channelDetails.setProxyEnabled(true);
        channelDetails.setProxyHost("127.0.0.1");
        channelDetails.setProxyPort(Long.parseLong("8090"));
        channelDetails.setProxyUsername("username");
        channelDetails.setProxyPassword("setProxyPassword");
        channelDetails.setTargetHost("setTargetHost");
        channelDetails.setTargetPort(Long.parseLong("8888"));
        channelDetails.setTargetPath("setTargetPath");
        channelDetails.setThreadNumber(Long.parseLong("1"));
        channelDetails.setTimeoutA(Long.parseLong("1"));
        channelDetails.setTimeoutB(Long.parseLong("2"));
        channelDetails.setTimeoutC(Long.parseLong("3"));
        channelDetails.setNpmService("setNpmService");
        channelDetails.setNewFaultCode(false);
        channelDetails.setRedirectIp("127.0.0.3");
        channelDetails.setRedirectPath("setRedirectPath");
        channelDetails.setRedirectPort(Long.parseLong("8989"));
        channelDetails.setRedirectQueryString("/setRedirectQueryString");
        channelDetails.setRedirectProtocol(Protocol.HTTP);
        channelDetails.setPaymentModel(PaymentModel.IMMEDIATE);
        channelDetails.setServPlugin("setServPlugin");
        channelDetails.setRtPush(true);
        channelDetails.setOnUs(true);
        channelDetails.setCardChart(true);
        channelDetails.setRecovery(true);
        channelDetails.setDigitalStampBrand(true);
        channelDetails.setFlagIo(true);
        channelDetails.setAgid(true);
        channelDetails.setBrokerDescription("setBrokerDescription");
        channelDetails.setEnabled(true);
        channelDetails.setChannelCode("setChannelCode");

        when(apiConfigServiceMock.createChannel(any(), anyString()))
                .thenReturn(channelDetails);

        when(apiConfigServiceMock.createChannelPaymentType(any(), anyString(), anyString()))
                .thenReturn(pspChannelPaymentTypes);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL)
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
                .createChannel(channelDetails, xRequestId);
        verify(apiConfigServiceMock, times(1))
                .createChannelPaymentType(pspChannelPaymentTypes,channelCode, xRequestId);
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


}
