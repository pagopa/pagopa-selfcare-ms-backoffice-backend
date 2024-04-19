package it.pagopa.selfcare.pagopa.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.service.ApiManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class InstitutionControllerTest {

    private final String INSTITUTION_ID = "INSTITUTION_ID";
    private final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ApiManagementService apiManagementService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        when(apiManagementService.getInstitution(anyString()))
                .thenReturn(new Institution());
        when(apiManagementService.getInstitutions(null))
                .thenReturn(Collections.singletonList(new InstitutionDetail()));
        when(apiManagementService.getBrokerDelegation(anyString(),anyString(),any()))
                .thenReturn(Collections.singletonList(new Delegation()));
        when(apiManagementService.getInstitutionProducts(anyString()))
                .thenReturn(Collections.singletonList(new Product()));
        when(apiManagementService.getInstitutionApiKeys(anyString()))
                .thenReturn(Collections.singletonList(new InstitutionApiKeys()));
        when(apiManagementService.createSubscriptionKeys(any(), any()))
                .thenReturn(Collections.singletonList(new InstitutionApiKeys()));
    }

    @Test
    void getInstitutions() throws Exception {
        mvc.perform(get("/institutions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBrokerDelegation() throws Exception {
        mvc.perform(get("/institutions/delegations")
                        .queryParam("institution-id", "test1")
                        .queryParam("brokerId", "test1")
                        .queryParam("role","PSP")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getInstitution() throws Exception {
        mvc.perform(get("/institutions/{institution-id}", INSTITUTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getInstitutionProducts() throws Exception {
        mvc.perform(get("/institutions/{institution-id}/products", INSTITUTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getInstitutionApiKeys() throws Exception {
        mvc.perform(get("/institutions/{institution-id}/api-keys", INSTITUTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createSubscriptionKeys() throws Exception {
        mvc.perform(post("/institutions//{institution-id}/api-keys", INSTITUTION_ID)
                        .param("subscription-code", Subscription.GPD.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void regeneratePrimaryKey() throws Exception {
        mvc.perform(post("/institutions/{institution-id}/api-keys/{subscription-id}/primary/regenerate",
                        INSTITUTION_ID, SUBSCRIPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void regenerateSecondaryKey() throws Exception {
        mvc.perform(post("/institutions/{institution-id}/api-keys/{subscription-id}/secondary/regenerate",
                        INSTITUTION_ID, SUBSCRIPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
}
