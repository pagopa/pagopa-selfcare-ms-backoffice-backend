package it.pagopa.selfcare.pagopa.backoffice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionApiKeysResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.service.ApiManagementService;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
class InstitutionControllerTest {

    private static final String INSTITUTION_ID = "INSTITUTION_ID";
    private static final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ApiManagementService apiManagementService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        when(apiManagementService.getInstitutions(null))
                .thenReturn(InstitutionBaseResources.builder()
                        .institutions(Collections.singletonList(InstitutionBase.builder()
                                .id("1")
                                .description("some description")
                                .build()))
                        .build());
        when(apiManagementService.getBrokerDelegation(anyString(), anyString(), any()))
                .thenReturn(buildBrokerDelegationResource());
        when(apiManagementService.getInstitutionProducts(anyString()))
                .thenReturn(buildProductResource());
        when(apiManagementService.getInstitutionApiKeys(anyString()))
                .thenReturn(buildInstitutionApiKeysResource());
        when(apiManagementService.createSubscriptionKeys(any(), any()))
                .thenReturn(buildInstitutionApiKeysResource());
        when(apiManagementService.getInstitutionFullDetail(any())).thenReturn(
                InstitutionDetail.builder().id("1")
                        .taxCode("tax")
                        .externalId("ext")
                        .originId("test")
                        .origin("test")
                        .description("someDescription").build());
    }

    @Test
    void getInstitutions() throws Exception {
        mvc.perform(get("/institutions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getInstitutionsFullDetail() throws Exception {
        mvc.perform(get("/institutions/{institution-id}/full-detail", INSTITUTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBrokerDelegation() throws Exception {
        mvc.perform(get("/institutions/delegations")
                        .queryParam("institution-id", "test1")
                        .queryParam("brokerId", "test1")
                        .queryParam("role", "PSP")
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

    @Test
    void saveServiceConsent_shouldReturn200WithResponse() throws Exception {
        ServiceConsent consent = ServiceConsent.OPT_OUT;
        OffsetDateTime now = OffsetDateTime.now();

        String requestBody = String.format("""
            {
                "consent": "%s"
            }
            """, consent.name());

        MvcResult mvcResult = mvc.perform(put("/institutions/{institution-id}/services/{service-id}/consent",
                        INSTITUTION_ID, ServiceId.RTP.name())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ServiceConsentResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ServiceConsentResponse.class
        );

        assertNotNull(response);
        assertEquals(consent, response.getConsent());
        assertTrue(now.isBefore(response.getDate()));
    }

    @Test
    void saveServiceConsent_shouldReturn400_whenConsentIsMissing() throws Exception {
        String requestBody = """
            {
            }
            """;

        mvc.perform(put("/institutions/{institution-id}/services/{service-id}/consent",
                        INSTITUTION_ID, ServiceId.RTP.name())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getServiceConsents_shouldReturn200WithResponse() throws Exception {
        ServiceId service = ServiceId.RTP;
        ServiceConsent consent = ServiceConsent.OPT_OUT;
        OffsetDateTime now = OffsetDateTime.now();

        MvcResult mvcResult = mvc.perform(get("/institutions/{institution-id}/services/consents",
                        INSTITUTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ServiceConsentsResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ServiceConsentsResponse.class
        );

        assertNotNull(response);
        assertFalse(response.getServices().isEmpty());
        assertEquals(service, response.getServices().get(0).getServiceId());
        assertEquals(consent, response.getServices().get(0).getServiceConsent());
        assertTrue(now.isBefore(response.getServices().get(0).getConsentDate()));
    }

    private InstitutionApiKeysResource buildInstitutionApiKeysResource() {
        return InstitutionApiKeysResource.builder()
                .institutionApiKeys(
                        Collections.singletonList(
                                InstitutionApiKeys.builder()
                                        .displayName("displayName")
                                        .secondaryKey("secondaryKey")
                                        .primaryKey("primaryKey")
                                        .id("id")
                                        .build()
                        )
                ).build();
    }

    private ProductResource buildProductResource() {
        return ProductResource.builder()
                .products(
                        Collections.singletonList(
                                Product.builder()
                                        .id("0001")
                                        .description("Product_Description")
                                        .title("title")
                                        .build()
                        )
                ).build();
    }

    private DelegationResource buildBrokerDelegationResource() {
        return DelegationResource.builder()
                .delegations(Collections.singletonList(new Delegation()))
                .build();
    }
}
