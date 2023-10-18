package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.BrokerPspDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
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

import java.io.InputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {PaymentServiceProviderController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        PaymentServiceProviderController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class PaymentServiceProviderControllerTest {

    private static final String BASE_URL = "/payment-service-provider";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigService;

    @Test
    void updatePSP(@Value("classpath:stubs/paymentServiceProviderDetailsDto.json") Resource dto) throws Exception {
        //given
        String pspcode = "pspcode";
        InputStream is = dto.getInputStream();
        PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto = objectMapper.readValue(is, PaymentServiceProviderDetailsDto.class);
        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);

        when(apiConfigService.updatePSP(anyString(), any()))
                .thenReturn(paymentServiceProviderDetails);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL+ "/{pspcode}", pspcode)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        //then
        verify(apiConfigService, times(1))
                .updatePSP(anyString(), any());
        verifyNoMoreInteractions(apiConfigService);
    }

    @Test
    void updateBrokerPSP(@Value("classpath:stubs/brokerDto.json") Resource dto) throws Exception {
        //given
        String pspcode = "pspcode";
        InputStream is = dto.getInputStream();
        BrokerPspDetailsDto brokerPspDetailsDto = objectMapper.readValue(is, BrokerPspDetailsDto.class);
        BrokerPspDetails brokerPspDetails = ChannelMapper.fromBrokerPspDetailsDto(brokerPspDetailsDto);

        when(apiConfigService.updateBrokerPSP(anyString(), any()))
                .thenReturn(brokerPspDetails);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL+ "/brokerpsp/{brokercode}", pspcode)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        //then
        verify(apiConfigService, times(1))
                .updateBrokerPSP(anyString(), any());
        verifyNoMoreInteractions(apiConfigService);
    }

}
