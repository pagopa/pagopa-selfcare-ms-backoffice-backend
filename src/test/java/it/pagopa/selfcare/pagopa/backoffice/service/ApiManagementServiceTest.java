package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Subscription;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private AuthorizerConfigClient authorizerConfigClient;

    @Autowired
    private ApiManagementService service;

    @Test
    void getInstitutions() {
        when(externalApiClient.getInstitutions(any()))
                .thenReturn(Collections.singletonList(new InstitutionInfo()));
        List<InstitutionDetail> institutions = service.getInstitutions();
        assertNotNull(institutions);
        assertFalse(institutions.isEmpty());
        verify(externalApiClient).getInstitutions(any());
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
        List<Product> products = service.getInstitutionProducts(INSTITUTION_ID);
        assertNotNull(products);
    }

    @Test
    void getBrokerDelegationPSP() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any())).thenReturn(createDelegations());
        List<Delegation> delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, Collections.singletonList(RoleType.PSP));
        assertNotNull(delegations);
        assertEquals(1, delegations.size());
        assertEquals("00001", delegations.get(0).getBrokerId());
    }

    @Test
    void getBrokerDelegationEC() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any())).thenReturn(createDelegations());
        List<Delegation> delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, Collections.singletonList(RoleType.EC));
        assertNotNull(delegations);
        assertEquals(1, delegations.size());
        assertEquals("00002", delegations.get(0).getBrokerId());
    }

    @Test
    void getBrokerDelegationCombinedRoles() {
        when(externalApiClient.getBrokerDelegation(any(), any(), any(), any())).thenReturn(createDelegations());
        List<Delegation> delegations = service.getBrokerDelegation(INSTITUTION_ID, BROKER_ID, List.of(RoleType.EC,RoleType.PSP));
        assertNotNull(delegations);
        assertEquals(2, delegations.size());
    }

    @Test
    void getInstitutionApiKeys() {
        when(apimClient.getInstitutionApiKeys(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));
        List<InstitutionApiKeys> institutionApiKeys = service.getInstitutionApiKeys(INSTITUTION_ID);
        assertNotNull(institutionApiKeys);
        verify(apimClient).getInstitutionApiKeys(any());
    }

    @Test
    void createSubscriptionKeys() throws IOException {
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json", InstitutionResponse.class));
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));
        List<InstitutionApiKeys> institutionApiKeys = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.GPD);
        assertNotNull(institutionApiKeys);
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

        List<InstitutionApiKeys> institutionApiKeys = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.GPD);

        assertNotNull(institutionApiKeys);
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

        List<InstitutionApiKeys> result = service.createSubscriptionKeys(INSTITUTION_ID, Subscription.BO_EXT_EC);

        assertNotNull(result);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, times(2)).createAuthorization(any());
    }

    @Test
    void regeneratePrimaryKey() {
        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, SUBSCRIPTION_ID));

        verify(apimClient).regeneratePrimaryKey(SUBSCRIPTION_ID);
        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).deleteAuthorization(anyString());
        verify(authorizerConfigClient, never()).createAuthorization(any());
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
    void regenerateSecondaryKey() {
        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, SUBSCRIPTION_ID));

        verify(apimClient).regenerateSecondaryKey(SUBSCRIPTION_ID);
        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).deleteAuthorization(anyString());
        verify(authorizerConfigClient, never()).createAuthorization(any());
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