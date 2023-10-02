package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigSelfcareIntegrationService;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.core.JiraServiceManagerService;
import it.pagopa.selfcare.pagopa.backoffice.core.WrapperService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.StationMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {UtilsController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        UtilsController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
public class UtilsControllerTest {

    private static final String BASE_URL = "/utils";
    @Autowired
    protected MockMvc mvc;
    private final StationMapper mapper = Mappers.getMapper(StationMapper.class);
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    JiraServiceManagerService jiraServiceManagerService;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @MockBean
    private ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService;

    @MockBean
    private WrapperService wrapperServiceMock;

    @Test
    void getBrokerOrPspDetails() throws Exception {
        //given
        String brokerpspcode = "brokerpspcode";

        BrokerPspDetails brokerPspDetails = mockInstance(new BrokerPspDetails());
        PaymentServiceProviderDetails paymentServiceProviderDetails = mockInstance(new PaymentServiceProviderDetails());


        when(apiConfigServiceMock.getBrokerPsp(anyString()))
                .thenReturn(brokerPspDetails);
        when(apiConfigServiceMock.getPSPDetails(anyString()))
                .thenReturn(paymentServiceProviderDetails);
        //when
        mvc.perform(get(BASE_URL+"/psp-brokers/{code}/details", brokerpspcode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        //then
        verify(apiConfigServiceMock, times(1))
                .getBrokerPsp(anyString());
        verify(apiConfigServiceMock, times(1))
                .getPSPDetails(anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getBrokerAndPspDetails_NoDataFound() throws Exception {
        // given
        String brokerPspCode = "brokerPspCode";

        when(apiConfigServiceMock.getBrokerPsp(anyString()))
                .thenReturn(null);
        when(apiConfigServiceMock.getPSPDetails(anyString()))
                .thenReturn(null);

        // when
        mvc.perform(get(BASE_URL+"/psp-brokers/{code}/details", brokerPspCode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        // then
        verify(apiConfigServiceMock, times(1))
                .getBrokerPsp(anyString());
        verify(apiConfigServiceMock, times(1))
                .getPSPDetails(anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getBrokerAndEcDetails() throws Exception {
        //given
        String brokerECcode = "brokerECcode";

        Broker broker = new Broker();
        broker.setBrokerCode(brokerECcode);
        Brokers brokers = mockInstance(new Brokers());
        brokers.setBrokerList(new ArrayList<>());
        brokers.getBrokerList().add(broker);
        CreditorInstitutionDetails creditorInstitutionDetails = mockInstance(new CreditorInstitutionDetails());


        when(apiConfigServiceMock.getBrokersEC(anyInt(), anyInt(), anyString(), eq(null), eq(null), anyString()))
                .thenReturn(brokers);
        when(apiConfigServiceMock.getCreditorInstitutionDetails(anyString()))
                .thenReturn(creditorInstitutionDetails);
        //when
        mvc.perform(get(BASE_URL+"/ec-brokers/{code}/details", brokerECcode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        //then
        verify(apiConfigServiceMock, times(1))
                .getBrokersEC(anyInt(), anyInt(), anyString(), any(), any(), anyString());
        verify(apiConfigServiceMock, times(1))
                .getCreditorInstitutionDetails(anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getBrokerAndEcDetails_NoDataFound() throws Exception {
        // given
        String brokerECcode = "brokerECcode";

        when(apiConfigServiceMock.getBrokersEC(anyInt(), anyInt(), anyString(), eq(null), eq(null), anyString()))
                .thenReturn(new Brokers());
        when(apiConfigServiceMock.getCreditorInstitutionDetails(anyString()))
                .thenReturn(null);

        // when
        mvc.perform(get(BASE_URL + "/ec-brokers/{code}/details", brokerECcode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

        // then
        verify(apiConfigServiceMock, times(1))
                .getBrokersEC(anyInt(), anyInt(), anyString(), any(), any(), anyString());
        verify(apiConfigServiceMock, times(1))
                .getCreditorInstitutionDetails(anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

}
