package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerPspDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokersPspResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelDetailsResourceList;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProvidersResource;
import it.pagopa.selfcare.pagopa.backoffice.service.PspBrokerService;
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
class PspBrokerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PspBrokerService pspBrokerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        when(pspBrokerService.getBrokersForPSP(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new BrokersPspResource());
        when(pspBrokerService.getBrokerForPsp(anyString())).thenReturn(new BrokerPspDetailsResource());
        when(pspBrokerService.createBrokerForPSP(any())).thenReturn(new BrokerPspDetailsResource());
        when(pspBrokerService.getChannelByBroker(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(new ChannelDetailsResourceList());
        when(pspBrokerService.getPSPAssociatedToBroker(anyString(), anyInt(), anyInt()))
                .thenReturn(new PaymentServiceProvidersResource());
    }

    @Test
    void getBrokersPsp() throws Exception {
        String url = "/psp-brokers";
        mvc.perform(get(url)
                        .param("brokerTaxCode", "brokerTaxCode")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBrokerPsp() throws Exception {
        String url = "/psp-brokers/{broker-tax-code}";
        mvc.perform(get(url, "brokerTaxCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createBroker() throws Exception {
        String url = "/psp-brokers";
        mvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(new BrokerPspDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void updateBrokerPSP() throws Exception {
        String url = "/psp-brokers/{broker-tax-code}";
        mvc.perform(put(url, "brokerTaxCode")
                        .content(objectMapper.writeValueAsString(new BrokerPspDetailsDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getChannelByBroker() throws Exception {
        String url = "/psp-brokers/{broker-tax-code}/channels";
        mvc.perform(get(url, "brokerTaxCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getPspBrokerPsp() throws Exception {
        String url = "/psp-brokers/{broker-tax-code}/payment-service-providers";
        mvc.perform(get(url, "brokerTaxCode")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deletePspBroker() throws Exception {
        String url = "/psp-brokers/{broker-tax-code}";
        mvc.perform(delete(url, "brokerCode", "ciTaxCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}