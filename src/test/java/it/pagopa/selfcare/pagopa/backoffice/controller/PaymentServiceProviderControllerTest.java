package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerOrPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProviderDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProviderDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProvidersResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.service.PaymentServiceProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class PaymentServiceProviderControllerTest {

    private static final String PSP_CODE = "PSP-code";
    private static final String PSP_TAX_CODE = "PSP-tax-code";
    private static final String CHANNEL_CODE = "channel-code";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PaymentServiceProviderService pspServiceMock;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        when(pspServiceMock.getPaymentServiceProviders(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(new PaymentServiceProvidersResource());
        when(pspServiceMock.getBrokerAndPspDetails(anyString()))
                .thenReturn(new BrokerOrPspDetailsResource());
        when(pspServiceMock.getPSPChannels(anyString()))
                .thenReturn(new PspChannelsResource());
        when(pspServiceMock.getFirstValidChannelCode(anyString(), anyBoolean()))
                .thenReturn(new ChannelCodeResource(""));
        when(pspServiceMock.createPSP(any(), anyBoolean()))
                .thenReturn(new PaymentServiceProviderDetailsResource());
        when(pspServiceMock.updatePSP(anyString(), any()))
                .thenReturn(new PaymentServiceProviderDetailsResource());
        when(pspServiceMock.updatePSPChannel(anyString(), anyString(), any()))
                .thenReturn(new PspChannelPaymentTypesResource());
    }

    @Test
    void getPaymentServiceProvidersTest() throws Exception {
        mvc.perform(get("/payment-service-providers")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBrokerAndPspDetailsTest() throws Exception {
        mvc.perform(get("/payment-service-providers/{psp-code}", PSP_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPspChannelsTest() throws Exception {
        mvc.perform(get("/payment-service-providers/{psp-tax-code}/channels", PSP_TAX_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getFirstValidChannelCodeTest() throws Exception {
        mvc.perform(get("/payment-service-providers/{psp-code}/channels/available-code", PSP_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createPSPTest() throws Exception {
        mvc.perform(post("/payment-service-providers")
                        .content(objectMapper.writeValueAsBytes(new PaymentServiceProviderDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePSPTest() throws Exception {
        mvc.perform(put("/payment-service-providers/{psp-code}", PSP_CODE)
                        .content(objectMapper.writeValueAsBytes(new PaymentServiceProviderDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updatePaymentServiceProvidersChannelsTest() throws Exception {
        mvc.perform(put("/payment-service-providers//{psp-code}/channels/{channel-code}", PSP_CODE, CHANNEL_CODE)
                        .content(objectMapper.writeValueAsBytes(new PspChannelPaymentTypes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deletePSPChannelsTest() throws Exception {
        mvc.perform(delete("/payment-service-providers//{psp-code}/channels/{channel-code", PSP_CODE, CHANNEL_CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}