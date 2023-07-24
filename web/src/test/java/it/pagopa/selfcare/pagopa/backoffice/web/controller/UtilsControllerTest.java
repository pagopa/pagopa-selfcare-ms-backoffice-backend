package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
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
    private StationMapper mapper = Mappers.getMapper(StationMapper.class);
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


        when(apiConfigServiceMock.getBrokerPsp(anyString(), anyString()))
                .thenReturn(brokerPspDetails);
        when(apiConfigServiceMock.getPSPDetails(anyString(), anyString()))
                .thenReturn(paymentServiceProviderDetails);
        //when
        mvc.perform(get(BASE_URL+"/broker-or-psp-details")
                        .queryParam("brokerpspcode", brokerpspcode)

                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        //then
        verify(apiConfigServiceMock, times(1))
                .getBrokerPsp(anyString(), anyString());
        verify(apiConfigServiceMock, times(1))
                .getPSPDetails(anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

}
