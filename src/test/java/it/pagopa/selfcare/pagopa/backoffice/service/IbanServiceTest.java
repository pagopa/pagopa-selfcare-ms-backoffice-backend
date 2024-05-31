package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreateApiconfig;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class IbanServiceTest {

    @MockBean
    private ExternalApiClient externalApiClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @Autowired
    @InjectMocks
    private IbanService ibanService;

    @Autowired
    private MockMvc mvc;


    @Test
    void getIban() throws IOException {
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(eq("1111"), any()))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/ibans.json", Ibans.class));
        Ibans response = ibanService.getIban("1111", null);
        assertNotNull(response);
        assertNotNull(response.getIbanList());
        assertEquals(28, response.getIbanList().size());
    }

    @Test
    void getCreditorInstitutionIbans() throws Exception {
        String url = "/creditor-institutions/11111/ibans";
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionIbans(eq("11111"), any()))
                .thenReturn(TestUtil.fileToObject("response/apiconfig/ibans.json", Ibans.class));
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCreditorInstitutionIbans() throws Exception {
        String url = "/creditor-institutions/11111/ibans";
        when(apiConfigClient.createCreditorInstitutionIbans(any(), any()))
                .thenReturn(TestUtil.fileToObject("request/create_iban.json", IbanCreateApiconfig.class));
        mvc.perform(post(url).content(TestUtil.readJsonFromFile("request/create_iban.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
