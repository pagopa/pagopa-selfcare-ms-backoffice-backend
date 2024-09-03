package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institutions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {MappingsConfiguration.class, ApiManagementService.class})
class ApiManagementServiceTest {

    private final String INSTITUTION_ID = "INSTITUTION_ID";
    private final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";
    private final String BROKER_ID = "BROKER_ID";

    @MockBean
    private AzureApiManagerClient apimClient;

    @MockBean
    private ExternalApiClient externalApiClient;

    @MockBean
    private FeatureManager featureManager;

    @MockBean
    private AuthorizerConfigClient authorizerConfigClient;

    @Autowired
    private ApiManagementService service;

    @Test
    void getInstitutions() {
        when(externalApiClient.getInstitutions(any()))
                .thenReturn(Collections.singletonList(InstitutionInfo.builder()
                        .userProductRoles(List.of("admin"))
                        .build()));
        InstitutionDetailResource institutions = service.getInstitutions(null);
        assertNotNull(institutions);
        assertNotNull(institutions.getInstitutionDetails());
        assertFalse(institutions.getInstitutionDetails().isEmpty());
        verify(externalApiClient).getInstitutions(any());
    }

    @Test
    void getInstitutionsFilteredByName() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution elem = it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.builder()
                .id("1")
                .institutionType("PA")
                .build();
        Institutions body = Institutions.builder()
                .institutions(Collections.singletonList(elem))
                .build();
        when(externalApiClient.getInstitutionsFiltered(any()))
                .thenReturn(body);
        when(featureManager.isEnabled(anyString())).thenReturn(true);
        InstitutionDetailResource institutions = service.getInstitutions("123BCS");
        assertNotNull(institutions);
        assertNotNull(institutions.getInstitutionDetails());
        assertFalse(institutions.getInstitutionDetails().isEmpty());
        verify(externalApiClient, never()).getInstitutions(any());
    }

    @Test
    void getInstitution() throws IOException {
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json", InstitutionResponse.class));
        Institution institution = service.getInstitution(INSTITUTION_ID);
        assertNotNull(institution);
        verify(externalApiClient).getInstitution(any());
    }

    @Test
    void getInstitutionProducts() {
        when(externalApiClient.getInstitutionUserProducts(any(), any())).thenReturn(Collections.singletonList(
                Product.builder().id("0001").description("Product_Description").build()));
        ProductResource products = service.getInstitutionProducts(INSTITUTION_ID);
        assertNotNull(products);
        assertNotNull(products.getProducts());
    }

    @Test
    void getBrokerDelegationPSP() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any(), eq(null))).thenReturn(createDelegations());
        DelegationResource delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, Collections.singletonList(RoleType.PSP));
        assertNotNull(delegations);
        assertNotNull(delegations.getDelegations());
        assertEquals(1, delegations.getDelegations().size());
        assertEquals("00001", delegations.getDelegations().get(0).getBrokerId());
    }

    @Test
    void getBrokerDelegationEC() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any(), eq(null))).thenReturn(createDelegations());
        DelegationResource delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, Collections.singletonList(RoleType.CI));
        assertNotNull(delegations);
        assertNotNull(delegations.getDelegations());
        assertEquals(1, delegations.getDelegations().size());
        assertEquals("00002", delegations.getDelegations().get(0).getBrokerId());
    }

    @Test
    void getBrokerDelegationCombinedRoles() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any(), eq(null))).thenReturn(createDelegations());
        DelegationResource delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, List.of(RoleType.CI, RoleType.PSP));
        assertNotNull(delegations);
        assertNotNull(delegations.getDelegations());
        assertEquals(2, delegations.getDelegations().size());
    }

    @Test
    void getInstitutionApiKeys() {
        when(apimClient.getInstitutionApiKeys(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));

        InstitutionApiKeysResource institutionApiKeys = service.getInstitutionApiKeys(INSTITUTION_ID);

        assertNotNull(institutionApiKeys);
        assertNotNull(institutionApiKeys.getInstitutionApiKeys());

        verify(apimClient).getInstitutionApiKeys(any());
    }

    @Test
    void createSubscriptionKeys() throws IOException {
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json", InstitutionResponse.class));
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));

        InstitutionApiKeysResource institutionApiKeys = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.BIZ);

        assertNotNull(institutionApiKeys);
        assertNotNull(institutionApiKeys.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
    }

    @Test
    void createSubscriptionKeysWithoutAPIMUser() throws IOException {
        when(apimClient.getInstitution(INSTITUTION_ID)).thenThrow(IllegalArgumentException.class);
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json", InstitutionResponse.class));
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));

        InstitutionApiKeysResource institutionApiKeys = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.BIZ);

        assertNotNull(institutionApiKeys);
        assertNotNull(institutionApiKeys.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
    }

    @Test
    void createSubscriptionKeysForBOExtEC() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        InstitutionApiKeys institutionApiKeys =
                buildInstitutionApiKeys(String.format("%s%s", Subscription.BO_EXT_EC.getPrefixId(), institutionResponse.getTaxCode()));

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));

        InstitutionApiKeysResource result = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.BO_EXT_EC);

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, times(2)).createAuthorization(any());
    }

    @Test
    void createSubscriptionKeysForFdrPsp() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        InstitutionApiKeys institutionApiKeys =
                buildInstitutionApiKeys(String.format("%s%s", Subscription.FDR_PSP.getPrefixId(), institutionResponse.getTaxCode()));

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));

        InstitutionApiKeysResource result = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.FDR_PSP);

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, times(2)).createAuthorization(any());
    }

    @Test
    void regeneratePrimaryKey() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys("gdp-123456");
        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder()
                .id("auth-id")
                .build());
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, "gdp-123456"));

        verify(apimClient).regeneratePrimaryKey("gdp-123456");
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization("auth-id");
        verify(authorizerConfigClient).createAuthorization(any());
    }

    @Test
    void regeneratePrimaryKeyForBOExtEC() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        String subscriptionId = String.format("%s%s", Subscription.BO_EXT_EC.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder().id("auth-id").build());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(anyString());
        verify(authorizerConfigClient).createAuthorization(any());
    }

    @Test
    void regeneratePrimaryKeyForBOExtPSP() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        String subscriptionId = String.format("%s%s", Subscription.BO_EXT_PSP.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder().id("auth-id").build());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(anyString());
        verify(authorizerConfigClient).createAuthorization(any());
    }

    @Test
    void regenerateSecondaryKey() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys("gdp-123456");

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder()
                .id("auth-id")
                .build());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, "gdp-123456"));

        verify(apimClient).regenerateSecondaryKey("gdp-123456");
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization("auth-id");
        verify(authorizerConfigClient).createAuthorization(any());
    }

    @Test
    void regenerateSecondaryKeyForBOExtEC() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        String subscriptionId = String.format("%s%s", Subscription.BO_EXT_EC.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder().id("auth-id").build());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regenerateSecondaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(anyString());
        verify(authorizerConfigClient).createAuthorization(any());
    }

    @Test
    void regenerateSecondaryKeyForBOExtPSP() throws IOException {
        InstitutionResponse institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", InstitutionResponse.class);
        String subscriptionId = String.format("%s%s", Subscription.BO_EXT_PSP.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder().id("auth-id").build());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regenerateSecondaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(anyString());
        verify(authorizerConfigClient).createAuthorization(any());
    }

    private InstitutionApiKeys buildInstitutionApiKeys(String subscriptionId) {
        InstitutionApiKeys institutionApiKeys = new InstitutionApiKeys();
        institutionApiKeys.setId(subscriptionId);
        institutionApiKeys.setPrimaryKey("primaryKey");
        institutionApiKeys.setSecondaryKey("secondaryKey");
        return institutionApiKeys;
    }

    public List<DelegationExternal> createDelegations() {
        List<DelegationExternal> delegationExternals = new ArrayList<>();
        delegationExternals.add(
                DelegationExternal
                        .builder()
                        .id("00001")
                        .brokerId("00001")
                        .brokerName("BrokerPsp")
                        .brokerTaxCode("000001")
                        .brokerType("TypePSP")
                        .institutionId("0001")
                        .institutionName("Institution Psp 1")
                        .institutionRootName("Institution Root Name Psp 1")
                        .institutionType("PSP")
                        .build()
        );
        delegationExternals.add(
                DelegationExternal
                        .builder()
                        .id("00002")
                        .brokerId("00002")
                        .brokerName("BrokerEC")
                        .brokerTaxCode("000002")
                        .brokerType("TypeEC")
                        .institutionId("0002")
                        .institutionName("Institution EC 1")
                        .institutionRootName("Institution Root Name EC 1")
                        .institutionType("SCP")
                        .build()
        );
        return delegationExternals;
    }

}
