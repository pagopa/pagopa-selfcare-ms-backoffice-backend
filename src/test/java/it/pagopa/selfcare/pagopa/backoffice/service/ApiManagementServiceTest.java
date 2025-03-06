package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.component.ApiManagementComponent;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationGenericKeyValue;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationMetadata;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CIStationSegregationCodesList;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionStationSegregationCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionApiKeysResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBaseResources;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ProductResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Subscription;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institutions;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitutionProduct;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserProductStatus;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.PAGOPA_BACKOFFICE_PRODUCT_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {MappingsConfiguration.class, ApiManagementService.class})
class ApiManagementServiceTest {

    private static final String TAX_CODE_1 = "taxCode1";
    private static final String TAX_CODE_2 = "taxCode2";
    private static final String PSP_CODE_1 = "PSPCode1";
    private static final String PSP_CODE_2 = "PSPCode2";
    private static final String INSTITUTION_TAX_CODE = "aTaxCode";
    private static final String INSTITUTION_ID = "INSTITUTION_ID";
    private static final String BROKER_ID = "BROKER_ID";
    private static final String CI_TAX_CODE = "ciTaxCode";

    private static final String AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY = "_seg";
    private static final String AUTH_ID = "auth-id";

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

    @MockBean
    private LegacyPspCodeUtil legacyPspCodeUtil;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @Captor
    private ArgumentCaptor<Authorization> authorizationCaptor;

    @Autowired
    private ApiManagementService service;

    @Test
    void getInstitutionsSuccess() {
        when(externalApiClient.getUserInstitution(any(), any(), any(), any(), any(), eq(0), any()))
                .thenReturn(
                        Collections.singletonList(
                                UserInstitution.builder()
                                        .institutionId("test")
                                        .institutionDescription("test")
                                        .products(
                                                Collections.singletonList(
                                                        UserInstitutionProduct.builder()
                                                                .productId(PAGOPA_BACKOFFICE_PRODUCT_ID)
                                                                .status(UserProductStatus.ACTIVE)
                                                                .productRole("admin")
                                                                .build())
                                        )
                        .build()));
        InstitutionBaseResources institutions = service.getInstitutions(null);

        assertNotNull(institutions);
        assertNotNull(institutions.getInstitutions());
        assertEquals(1, institutions.getInstitutions().size());

        verify(externalApiClient, times(2)).getUserInstitution(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getInstitutionsSuccessWithEmptyListBecauseInactiveBackofficeProduct() {
        when(externalApiClient.getUserInstitution(any(), any(), any(), any(), any(), eq(0), any()))
                .thenReturn(
                        Collections.singletonList(
                                UserInstitution.builder()
                                        .institutionId("test")
                                        .institutionDescription("test")
                                        .products(
                                                Collections.singletonList(
                                                        UserInstitutionProduct.builder()
                                                                .productId(PAGOPA_BACKOFFICE_PRODUCT_ID)
                                                                .status(UserProductStatus.DELETED)
                                                                .productRole("admin")
                                                                .build())
                                        )
                        .build()));
        when(externalApiClient.getUserInstitution(any(), any(), any(), any(), any(), eq(1), any()))
                .thenReturn(Collections.emptyList());

        InstitutionBaseResources institutions = service.getInstitutions(null);

        assertNotNull(institutions);
        assertNotNull(institutions.getInstitutions());
        assertTrue(institutions.getInstitutions().isEmpty());

        verify(externalApiClient, times(2)).getUserInstitution(any(), any(), any(), any(), any(), any(), any());
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
    }

    @Test
    void getInstitutionFullDetailForOperator() {
        when(featureManager.isEnabled("isOperator")).thenReturn(true);
        when(externalApiClient.getInstitution(anyString())).thenReturn(buildInstitutionResponse(InstitutionType.PA));

        InstitutionDetail institutionDetail = service.getInstitutionFullDetail(INSTITUTION_ID);

        assertNotNull(institutionDetail);

        verify(externalApiClient, never())
                .getUserInstitution(anyString(), anyString(), eq(null), eq(null), eq(null), eq(null), eq(null));
    }

    @Test
    void getInstitutionFullDetailForRegularUser() {
        when(featureManager.isEnabled("isOperator")).thenReturn(false);
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PA));
        when(externalApiClient.getUserInstitution(anyString(), anyString(), eq(null), eq(null), eq(null), eq(null), eq(null)))
                .thenReturn(List.of(
                        UserInstitution.builder()
                                .products(
                                        List.of(
                                                UserInstitutionProduct.builder()
                                                        .productId("prod-pagopa")
                                                        .status(UserProductStatus.ACTIVE)
                                                        .productRole("admin")
                                                        .productRoleLabel("administrator")
                                                        .build()
                                        )
                                )
                                .build()
                ));

        InstitutionDetail institutionDetail = service.getInstitutionFullDetail(INSTITUTION_ID);

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
    void createSubscriptionKeySuccessForNodeAuth() {
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PA));
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));

        InstitutionApiKeysResource result = assertDoesNotThrow(() ->
                service. createSubscriptionKeys(INSTITUTION_ID, Subscription.NODOAUTH));

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
    }

    @Test
    void createSubscriptionKeyFailPSPRequestCISubscription() {
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PSP));

        AppException e = assertThrows(AppException.class, () ->
                service.createSubscriptionKeys(INSTITUTION_ID, Subscription.GPD));

        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());

        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient, never()).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient, never()).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
    }

    @Test
    void createSubscriptionKeyFailCIRequestPSPSubscription() {
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PA));

        AppException e = assertThrows(AppException.class, () ->
                service.createSubscriptionKeys(INSTITUTION_ID, Subscription.FDR_PSP));

        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());

        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient, never()).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient, never()).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
    }

    @Test
    void createSubscriptionKeyFailPTCIRequestPSPSubscription() {
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PT));
        when(apiConfigClient.getBrokerPsp(anyString())).thenThrow(FeignException.NotFound.class);

        AppException e = assertThrows(AppException.class, () ->
                service.createSubscriptionKeys(INSTITUTION_ID, Subscription.FDR_PSP));

        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());

        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient, never()).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient, never()).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
    }

    @Test
    void createSubscriptionKeyFailPTPSPRequestCISubscription() {
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PT));
        when(apiConfigClient.getBroker(anyString())).thenThrow(FeignException.NotFound.class);

        AppException e = assertThrows(AppException.class, () ->
                service.createSubscriptionKeys(INSTITUTION_ID, Subscription.GPD));

        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());

        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient, never()).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient, never()).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"NODOAUTH", "BIZ", "PRINT_NOTICE"})
    void createSubscriptionKeysSuccessNoAuthorizer(Subscription sub) {
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PA));
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));

        InstitutionApiKeysResource result = assertDoesNotThrow(() ->
                service.createSubscriptionKeys(INSTITUTION_ID, sub));

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
    }

    @Test
    void createSubscriptionKeysWithoutAPIMUser() {
        when(apimClient.getInstitution(INSTITUTION_ID)).thenThrow(IllegalArgumentException.class);
        when(externalApiClient.getInstitution(any())).thenReturn(buildInstitutionResponse(InstitutionType.PA));
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(new InstitutionApiKeys()));

        InstitutionApiKeysResource result = assertDoesNotThrow(() ->
                service.createSubscriptionKeys(INSTITUTION_ID, Subscription.BIZ));

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
    }

    @ParameterizedTest
    @CsvSource({
            "BO_EXT_EC, PA",
            "BO_EXT_PSP, PSP"
    })
    void createSubscriptionKeysForBOExtEC(Subscription sub, InstitutionType instType) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(instType);
        InstitutionApiKeys institutionApiKeys =
                buildInstitutionApiKeys(String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode()));

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));

        InstitutionApiKeysResource result = assertDoesNotThrow(() ->
                service.createSubscriptionKeys(INSTITUTION_ID, sub));

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(authorizerConfigClient, times(2)).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getOwner());
        assertEquals(INSTITUTION_TAX_CODE, captorValue.getOwner().getId());
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(1, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> INSTITUTION_TAX_CODE.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"GPD", "GPD_REP", "GPD_PAY"})
    void createSubscriptionKeysForGPDSuccess(Subscription sub) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PA);
        InstitutionApiKeys institutionApiKeys =
                buildInstitutionApiKeys(String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode()));
        List<DelegationExternal> delegations = createDelegations();

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(delegations);
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

        InstitutionApiKeysResource result = assertDoesNotThrow(() ->
                service.createSubscriptionKeys(INSTITUTION_ID, sub));

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(authorizerConfigClient, times(2)).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getOwner());
        assertEquals(INSTITUTION_TAX_CODE, captorValue.getOwner().getId());
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(2, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> INSTITUTION_TAX_CODE.equals(elem.getValue())));
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> TAX_CODE_2.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertEquals(1, captorValue.getOtherMetadata().size());
        assertEquals(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY, captorValue.getOtherMetadata().get(0).getShortKey());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"FDR_PSP", "QI_FDR_KPI"})
    void createSubscriptionKeysForSubscriptionThatRequirePSPCodeFailNoPSPCodeFound(Subscription sub) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenThrow(AppException.class);

        AppException e = assertThrows(AppException.class, () ->
                service.createSubscriptionKeys(INSTITUTION_ID, sub));

        assertNotNull(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());

        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient, never()).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient, never()).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(legacyPspCodeUtil, times(1)).retrievePspCode(anyString(), anyBoolean());
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(authorizerConfigClient, never()).createAuthorization(any());
    }

    @Test
    void createSubscriptionKeysForFdrPspSuccessWithOneDelegationExcludedForNoPSPCode() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);
        InstitutionApiKeys institutionApiKeys =
                buildInstitutionApiKeys(String.format("%s%s", Subscription.FDR_PSP.getPrefixId(), institutionResponse.getTaxCode()));
        List<DelegationExternal> delegations = createDelegations();

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(delegations);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenReturn(PSP_CODE_1, PSP_CODE_1);
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE_1, false)).thenThrow(AppException.class);

        InstitutionApiKeysResource result = assertDoesNotThrow(() ->
                service.createSubscriptionKeys(INSTITUTION_ID, Subscription.FDR_PSP));

        assertNotNull(result);
        assertNotNull(result.getInstitutionApiKeys());

        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(apimClient).getInstitution(INSTITUTION_ID);
        verify(apimClient, never()).createInstitution(anyString(), any());
        verify(apimClient).createInstitutionSubscription(any(), any(), any(), any(), any());
        verify(externalApiClient).getInstitution(INSTITUTION_ID);
        verify(legacyPspCodeUtil, times(3)).retrievePspCode(anyString(), anyBoolean());
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(authorizerConfigClient, times(2)).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getOwner());
        assertEquals(INSTITUTION_TAX_CODE, captorValue.getOwner().getId());
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(1, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_1.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"NODOAUTH", "BIZ", "PRINT_NOTICE"})
    void regeneratePrimaryKeyForNoAuthSubscription(Subscription sub) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PA);
        String subscriptionId = String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode());

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient, never()).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"GPD", "GPD_REP", "GPD_PAY"})
    void regeneratePrimaryKeyForGPDSuccess(Subscription sub) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PA);
        String subscriptionId = String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenReturn(buildAuthorizationWithSegregationCodes(CI_TAX_CODE));
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(2, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> INSTITUTION_TAX_CODE.equals(elem.getValue())));
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> TAX_CODE_2.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertEquals(1, captorValue.getOtherMetadata().size());
        assertEquals(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY, captorValue.getOtherMetadata().get(0).getShortKey());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"FDR_PSP", "QI_FDR_KPI"})
    void regeneratePrimaryKeyForSubscriptionThatRequirePSPCodeFailNoPSPCodeFound(Subscription sub) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);
        String subscriptionId = String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode());

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenThrow(AppException.class);

        AppException e = assertThrows(AppException.class, () -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        assertNotNull(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());

        verify(apimClient, never()).regeneratePrimaryKey(subscriptionId);
        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(authorizerConfigClient, never()).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
    }

    @Test
    void regeneratePrimaryKeyForFDRPSPSuccess() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);
        String subscriptionId = String.format("%s%s", Subscription.FDR_PSP.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenReturn(buildAuthorizationWithSegregationCodes(CI_TAX_CODE));
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenReturn(PSP_CODE_1, PSP_CODE_1);
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE_1, false)).thenReturn(PSP_CODE_2);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(2, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_1.equals(elem.getValue())));
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_2.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
    }

    @Test
    void regeneratePrimaryKeyForQIFDRKPISuccess() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);
        String subscriptionId = String.format("%s%s", Subscription.QI_FDR_KPI.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenReturn(buildAuthorizationWithSegregationCodes(CI_TAX_CODE));
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenReturn(PSP_CODE_1, PSP_CODE_1);
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE_1, false)).thenReturn(PSP_CODE_2);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(3, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_1.equals(elem.getValue())));
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_2.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
    }

    @Test
    void regeneratePrimaryKeyFailOnAuthorizerConfigUpdateTriggerAPIKeyRecreation() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PA);
        String subscriptionId = String.format("%s%s", Subscription.GPD.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(apimClient.getApiSubscriptions(anyString()))
                .thenReturn(Collections.singletonList(institutionApiKeys))
                .thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenThrow(FeignException.NotFound.class)
                .thenReturn(Authorization.builder().id(AUTH_ID).build());
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(authorizerConfigClient).createAuthorization(any());
        verify(authorizerConfigClient, never()).deleteAuthorization(anyString());
    }

    @ParameterizedTest
    @CsvSource({
            "BO_EXT_EC, PA",
            "BO_EXT_PSP, PSP"
})
    void regeneratePrimaryKeyForBOExtEC(Subscription sub, InstitutionType instType) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(instType);
        String subscriptionId = String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder().id(AUTH_ID).build());

        assertDoesNotThrow(() -> service.regeneratePrimaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regeneratePrimaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(authorizerConfigClient).deleteAuthorization(anyString());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(1, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> INSTITUTION_TAX_CODE.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"GPD", "GPD_REP", "GPD_PAY"})
    void regenerateSecondaryKeyForGPDSuccess(Subscription sub) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PA);
        String subscriptionId = String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder()
                .id(AUTH_ID)
                .build());
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString()))
                .thenReturn(buildCreditorInstitutionStationSegregationCodesList());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, subscriptionId));

        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(apimClient).regenerateSecondaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(2, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> INSTITUTION_TAX_CODE.equals(elem.getValue())));
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> TAX_CODE_2.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertEquals(1, captorValue.getOtherMetadata().size());
        assertEquals(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY, captorValue.getOtherMetadata().get(0).getShortKey());
    }

    @ParameterizedTest
    @EnumSource(value = Subscription.class, names = {"FDR_PSP", "QI_FDR_KPI"})
    void regenerateSecondaryKeyForSubscriptionThatRequirePSPCodeFailNoPSPCodeFound(Subscription sub) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);
        String subscriptionId = String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode());

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenThrow(AppException.class);

        AppException e = assertThrows(AppException.class, () -> service.regenerateSecondaryKey(INSTITUTION_ID, subscriptionId));

        assertNotNull(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());

        verify(apimClient, never()).regenerateSecondaryKey(subscriptionId);
        verify(apimClient, never()).getApiSubscriptions(INSTITUTION_ID);
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(authorizerConfigClient, never()).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient, never()).createAuthorization(any());
    }

    @Test
    void regenerateSecondaryKeyForFDRPSPSubscriptionThatRequirePSPCodeSuccess() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);
        String subscriptionId = String.format("%s%s", Subscription.FDR_PSP.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenReturn(buildAuthorizationWithSegregationCodes(CI_TAX_CODE));
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenReturn(PSP_CODE_1, PSP_CODE_1);
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE_1, false)).thenReturn(PSP_CODE_2);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regenerateSecondaryKey(subscriptionId);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(2, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_1.equals(elem.getValue())));
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_2.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
    }

    @Test
    void regenerateSecondaryKeyForQIFDRKPISubscriptionThatRequirePSPCodeSuccess() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PSP);
        String subscriptionId = String.format("%s%s", Subscription.QI_FDR_KPI.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(apimClient.getApiSubscriptions(anyString())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString()))
                .thenReturn(buildAuthorizationWithSegregationCodes(CI_TAX_CODE));
        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(legacyPspCodeUtil.retrievePspCode(INSTITUTION_TAX_CODE, false)).thenReturn(PSP_CODE_1, PSP_CODE_1);
        when(legacyPspCodeUtil.retrievePspCode(TAX_CODE_1, false)).thenReturn(PSP_CODE_2);
        when(externalApiClient.getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null))
                .thenReturn(createDelegations());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, subscriptionId));

        verify(apimClient).regenerateSecondaryKey(subscriptionId);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(AUTH_ID);
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(3, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_1.equals(elem.getValue())));
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> PSP_CODE_2.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "BO_EXT_EC, PA",
            "BO_EXT_PSP, PSP"
    })
    void regenerateSecondaryKeyForBOExtEC(Subscription sub, InstitutionType instType) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(instType);
        String subscriptionId = String.format("%s%s", sub.getPrefixId(), institutionResponse.getTaxCode());
        InstitutionApiKeys institutionApiKeys = buildInstitutionApiKeys(subscriptionId);

        when(externalApiClient.getInstitution(any())).thenReturn(institutionResponse);
        when(apimClient.getApiSubscriptions(any())).thenReturn(Collections.singletonList(institutionApiKeys));
        when(authorizerConfigClient.getAuthorization(anyString())).thenReturn(Authorization.builder().id(AUTH_ID).build());

        assertDoesNotThrow(() -> service.regenerateSecondaryKey(INSTITUTION_ID, subscriptionId));

        verify(legacyPspCodeUtil, never()).retrievePspCode(anyString(), anyBoolean());
        verify(apimClient).regenerateSecondaryKey(subscriptionId);
        verify(apimClient).getApiSubscriptions(INSTITUTION_ID);
        verify(authorizerConfigClient).deleteAuthorization(anyString());
        verify(externalApiClient, never()).getBrokerDelegation(null, INSTITUTION_ID, "prod-pagopa", "FULL", null);
        verify(apiConfigSelfcareIntegrationClient, never()).getCreditorInstitutionsSegregationCodeAssociatedToBroker(anyString());
        verify(authorizerConfigClient).createAuthorization(authorizationCaptor.capture());

        Authorization captorValue = authorizationCaptor.getValue();
        assertNotNull(captorValue);
        assertNotNull(captorValue.getAuthorizedEntities());
        assertEquals(1, captorValue.getAuthorizedEntities().size());
        assertTrue(captorValue.getAuthorizedEntities().stream().anyMatch(elem -> INSTITUTION_TAX_CODE.equals(elem.getValue())));
        assertNotNull(captorValue.getOtherMetadata());
        assertTrue(captorValue.getOtherMetadata().isEmpty());
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
    void updateBrokerAuthorizerSegregationCodesMetadataFailOnPrimary() {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institutionResponse = buildInstitutionResponse(InstitutionType.PA);
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
                .shortKey(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY)
                .content(keyValues)
                .build());
        return Authorization.builder()
                .id(AUTH_ID)
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
                        .taxCode(TAX_CODE_1)
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
                        .taxCode(TAX_CODE_2)
                        .build()
        );
        return delegationExternals;
    }

    private CIStationSegregationCodesList buildCreditorInstitutionStationSegregationCodesList() {
        return CIStationSegregationCodesList.builder()
                .ciStationCodes(Collections.singletonList(
                        CreditorInstitutionStationSegregationCodes.builder()
                                .ciTaxCode(CI_TAX_CODE)
                                .segregationCodes(List.of("01", "14"))
                                .build()
                ))
                .build();
    }

    private it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution buildInstitutionResponse(
            InstitutionType institutionType
    ) {
        return it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution.builder()
                .id(INSTITUTION_ID)
                .externalId("000001")
                .origin("anOrigin")
                .institutionType(institutionType.name())
                .taxCode(INSTITUTION_TAX_CODE)
                .description("aDescription")
                .address("aAddress")
                .originId("123")
                .zipCode("aZipCode")
                .digitalAddress("aDigitalAddress")
                .build();
    }
}
