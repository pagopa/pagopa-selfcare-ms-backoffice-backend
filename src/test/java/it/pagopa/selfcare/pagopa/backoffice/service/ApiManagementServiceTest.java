package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.TestUtil;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.component.ApiManagementComponent;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationGenericKeyValue;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationMetadata;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionStationSegregationCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionStationSegregationCodesList;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institutions;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String CI_TAX_CODE = "ciTaxCode";

    @MockBean
    private AzureApiManagerClient apimClient;

    @MockBean
    private ExternalApiClient externalApiClient;

    @SpyBean
    private ApiManagementComponent apiManagementComponent;

    @MockBean
    private FeatureManager featureManager;

    @MockBean
    private AuthorizerConfigClient authorizerConfigClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    private ApiManagementService service;

    @Test
    void getInstitutions() {
        when(externalApiClient.getUserInstitution(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(UserInstitution.builder()
                        .institutionId("test").institutionDescription("test").products(Collections.emptyList())
                        .build()));
        InstitutionBaseResources institutions = service.getInstitutions(null);
        assertNotNull(institutions);
        assertNotNull(institutions.getInstitutions());
        assertFalse(institutions.getInstitutions().isEmpty());
        verify(externalApiClient).getUserInstitution(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getInstitutionsFilteredByName() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution elem =
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.builder()
                .id("1")
                .institutionType("PA")
                .build();
        Institutions body = Institutions.builder()
                .institutions(Collections.singletonList(elem))
                .build();
        when(externalApiClient.getInstitutionsFiltered(any()))
                .thenReturn(body);
        when(featureManager.isEnabled(anyString())).thenReturn(true);
        InstitutionBaseResources institutions = service.getInstitutions("123BCS");
        assertNotNull(institutions);
        assertNotNull(institutions.getInstitutions());
        assertFalse(institutions.getInstitutions().isEmpty());
        verify(externalApiClient, never()).getInstitutions(any());
    }

    @Test
    void getInstitution() throws IOException {
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json",
                        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class));
        Institution institution = service.getInstitution(INSTITUTION_ID);
        assertNotNull(institution);
        verify(externalApiClient).getInstitution(any());
    }

    @SneakyThrows
    @Test
    void getInstitutionFullDetail() {
        when(externalApiClient.getInstitution(any())).thenReturn(TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class));
        InstitutionDetail institutionDetail = service.getInstitutionFullDetail(any());
        assertNotNull(institutionDetail);
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
                        "response/externalapi/institution_response.json",
                        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class));
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
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
    }

    @Test
    void createSubscriptionKeysWithoutAPIMUser() throws IOException {
        when(apimClient.getInstitution(INSTITUTION_ID)).thenThrow(IllegalArgumentException.class);
        when(externalApiClient.getInstitution(any()))
                .thenReturn(TestUtil.fileToObject(
                        "response/externalapi/institution_response.json",
                        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class));
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
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
    }

    @Test
    void createSubscriptionKeysForBOExtEC() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
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
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
    }

    @Test
    void createSubscriptionKeysForFdrPsp() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
        InstitutionApiKeys institutionApiKeys =
                buildInstitutionApiKeys(String.format("%s%s", Subscription.FDR_PSP.getPrefixId(), institutionResponse.getTaxCode()));

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

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
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys("gdp-123456");
        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder()
                .id("auth-id")
                .build());
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, "gdp-123456"));

        verify(apimClient).regeneratePrimaryKey("gdp-123456");
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization("auth-id");
        verify(authorizerConfigClient).createAuthorization(any());
    }

    @Test
    void regeneratePrimaryKeyFailOnAuthorizerConfigUpdateTriggerAPIKeyRecreation() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys("gdp-aTaxCode");
        when(apimClient.getApiSubscriptions(anyString()))
                .thenReturn(Collections.singletonList(institutionApiKeys))
                .thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenThrow(FeignException.NotFound.class)
                .thenReturn(Authorization.builder().id("auth-id").build());
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, "gdp-aTaxCode"));

        verify(apimClient).regeneratePrimaryKey("gdp-aTaxCode");
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).createAuthorization(any());
        verify(authorizerConfigClient, never()).deleteAuthorization(anyString());
    }

    @Test
    void regeneratePrimaryKeyForBOExtEC() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
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
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
    }

    @Test
    void regeneratePrimaryKeyForBOExtPSP() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
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
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
    }

    @Test
    void regenerateSecondaryKey() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys("gdp-123456");

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder()
                .id("auth-id")
                .build());
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, "gdp-123456"));

        verify(apimClient).regenerateSecondaryKey("gdp-123456");
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization("auth-id");
        verify(authorizerConfigClient).createAuthorization(any());
    }

    @Test
    void regenerateSecondaryKeyForBOExtEC() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
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
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
    }

    @Test
    void regenerateSecondaryKeyForBOExtPSP() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json",
                it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
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
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
    }

    @Test
    void updateBrokerAuthorizerSegregationCodesMetadataSuccess() {
        String subscriptionId = String.format("%s%s", Subscription.GPD.getPrefixId(), CI_TAX_CODE);
        InstitutionApiKeys institutionApiKeys1 = buildInstitutionApiKeys(subscriptionId);
        InstitutionApiKeys institutionApiKeys2 = buildInstitutionApiKeys("not from BO API key");

        when(apimClient.getApiSubscriptions(INSTITUTION_ID)).thenReturn(List.of(institutionApiKeys1, institutionApiKeys2));
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(
                buildAuthorizationWithSegregationCodes(CI_TAX_CODE),
                buildAuthorizationWithSegregationCodes("pippo")
        );

        assertDoesNotThrow(() -> service.updateBrokerAuthorizerSegregationCodesMetadata(INSTITUTION_ID, CI_TAX_CODE));

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient, times(2)).updateAuthorization(anyString(), any());
    }

    @Test
    void updateBrokerAuthorizerSegregationCodesMetadataFailOnPrimary() throws IOException {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = TestUtil.fileToObject(
                "response/externalapi/institution_response.json", it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.class);
        String subscriptionId = String.format("%s%s", Subscription.GPD.getPrefixId(), CI_TAX_CODE);
        InstitutionApiKeys institutionApiKeys1 = buildInstitutionApiKeys(subscriptionId);

        when(apimClient.getApiSubscriptions(INSTITUTION_ID)).thenReturn(List.of(institutionApiKeys1));
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(
                        buildCreditorInstitutionStationSegregationCodesList(),
                        buildCreditorInstitutionStationSegregationCodesList()
                );
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenThrow(FeignException.NotFound.class)
                .thenReturn(buildAuthorizationWithSegregationCodes(CI_TAX_CODE));
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());

        assertDoesNotThrow(() -> service.updateBrokerAuthorizerSegregationCodesMetadata(INSTITUTION_ID, CI_TAX_CODE));

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).updateAuthorization(anyString(), any());
        verify(authorizerConfigClient).createAuthorization(any());
    }

    private Authorization buildAuthorizationWithSegregationCodes(String ciTaxCode) {
        List<AuthorizationGenericKeyValue> keyValues = new ArrayList<>();
        keyValues.add(
                AuthorizationGenericKeyValue.builder()
                        .key(ciTaxCode)
                        .values(Collections.singletonList("02"))
                        .build());
        List<AuthorizationMetadata> metadata = new ArrayList<>();
        metadata.add(AuthorizationMetadata.builder()
                .name("Segregation codes")
                .shortKey("_seg")
                .content(keyValues)
                .build());
        return Authorization.builder()
                .id("auth-id")
                .otherMetadata(metadata)
                .build();
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

    private CreditorInstitutionStationSegregationCodesList buildCreditorInstitutionStationSegregationCodesList() {
        return CreditorInstitutionStationSegregationCodesList.builder()
                .ciStationCodes(Arrays.asList(
                        CreditorInstitutionStationSegregationCodes.builder()
                                .ciTaxCode(CI_TAX_CODE)
                                .segregationCodes(List.of("01", "14"))
                                .build()
                ))
                .build();
    }
}
