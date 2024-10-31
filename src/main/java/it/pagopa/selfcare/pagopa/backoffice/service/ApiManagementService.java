package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.component.ApiManagementComponent;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationGenericKeyValue;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationMetadata;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationOwner;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CIStationSegregationCodesList;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionApiKeysResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBase;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionBaseResources;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ProductResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Subscription;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType.fromSelfcareRole;
import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.sanitizeLogParam;

@Slf4j
@Service
public class ApiManagementService {

    private static final String PRIMARY = "primary";
    private static final String SECONDARY = "secondary";
    private static final String AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY = "_seg";

    private final AzureApiManagerClient apimClient;

    private final ExternalApiClient externalApiClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final ModelMapper modelMapper;

    private final String testEmail;

    private final String environment;

    private final AuthorizerConfigClient authorizerConfigClient;

    private final FeatureManager featureManager;

    private final ApiManagementComponent apiManagementComponent;

    private final LegacyPspCodeUtil legacyPspCodeUtil;

    private final ApiConfigClient apiConfigClient;

    @Autowired
    public ApiManagementService(
            AzureApiManagerClient apimClient,
            ExternalApiClient externalApiClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            ModelMapper modelMapper,
            AuthorizerConfigClient authorizerConfigClient,
            FeatureManager featureManager,
            @Value("${institution.subscription.test-email}") String testEmail,
            @Value("${info.properties.environment}") String environment,
            ApiManagementComponent apiManagementComponent,
            LegacyPspCodeUtil legacyPspCodeUtil,
            ApiConfigClient apiConfigClient
    ) {
        this.apimClient = apimClient;
        this.externalApiClient = externalApiClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.modelMapper = modelMapper;
        this.testEmail = testEmail;
        this.environment = environment;
        this.authorizerConfigClient = authorizerConfigClient;
        this.featureManager = featureManager;
        this.apiManagementComponent = apiManagementComponent;
        this.legacyPspCodeUtil = legacyPspCodeUtil;
        this.apiConfigClient = apiConfigClient;
    }

    public InstitutionBaseResources getInstitutions(String taxCode) {
        List<InstitutionBase> institutionBases;
        if (taxCode != null && !taxCode.isEmpty()) {
            if (!Boolean.TRUE.equals(featureManager.isEnabled("isOperator"))) {
                throw new AppException(AppError.UNAUTHORIZED);
            }
            institutionBases = apiManagementComponent.getInstitutionsForOperator(taxCode);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userIdForAuth = Utility.extractUserIdFromAuth(authentication);
            institutionBases = apiManagementComponent.getInstitutions(userIdForAuth);
        }
        return InstitutionBaseResources.builder()
                .institutions(institutionBases)
                .build();
    }

    public InstitutionDetail getInstitutionFullDetail(String institutionId) {
        return apiManagementComponent.getInstitutionDetail(institutionId);
    }


    public Institution getInstitution(String institutionId) {
        return modelMapper.map(externalApiClient.getInstitution(institutionId), Institution.class);
    }

    public ProductResource getInstitutionProducts(String institutionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Product> institutionUserProducts = externalApiClient.getInstitutionUserProducts(
                institutionId, Utility.extractUserIdFromAuth(authentication));
        return ProductResource.builder()
                .products(institutionUserProducts)
                .build();
    }

    public DelegationResource getBrokerDelegation(String institutionId, String brokerId, List<RoleType> roles) {
        var response = externalApiClient.getBrokerDelegation(
                institutionId, brokerId, "prod-pagopa", "FULL", null);

        var result = response.stream()
                .map(elem -> modelMapper.map(elem, Delegation.class))
                .toList();

        // filter by roles
        if (roles != null && !roles.isEmpty()) {
            result = result.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(delegation -> {
                        log.info(delegation.toString());
                        RoleType roleType = fromSelfcareRole(delegation.getTaxCode(), delegation.getInstitutionType());
                        return roles.contains(roleType);
                    })
                    .toList();
        }
        return DelegationResource.builder()
                .delegations(result)
                .build();
    }

    public InstitutionApiKeysResource getInstitutionApiKeys(String institutionId) {
        List<InstitutionApiKeys> institutionApiKeys = apimClient.getInstitutionApiKeys(institutionId);
        return InstitutionApiKeysResource.builder()
                .institutionApiKeys(institutionApiKeys)
                .build();
    }

    /**
     * Create the subscription's api keys to the specified subscription for the specified institution.
     * <p>
     * If the subscription is for {@link Subscription#BO_EXT_EC} or {@link Subscription#BO_EXT_PSP} then it configure
     * the Authorizer config service in order to enable the authorization process.
     *
     * @param institutionId    the id of the institution
     * @param subscriptionCode the code of the requested subscription
     * @return the list of all institution's subscription's api keys
     */
    public InstitutionApiKeysResource createSubscriptionKeys(String institutionId, Subscription subscriptionCode) {
        InstitutionResponse institution = getInstitutionResponse(institutionId);
        checkIfInstitutionCanOperateOnSubscriptionOtherwiseThrowException(institution, subscriptionCode);
        if (subscriptionCode.equals(Subscription.FDR_PSP) && !InstitutionType.PT.equals(institution.getInstitutionType())) {
            checkIfPSPCodeIsAvailableOtherwiseThrowException(institution, subscriptionCode);
        }

        String subscriptionId = String.format("%s%s", subscriptionCode.getPrefixId(), institution.getTaxCode());
        String subscriptionName = buildSubscriptionName(subscriptionCode, institution);
        String subscriptionScope = String.format(subscriptionCode.getScope(), getEnvironment());
        createUserIfNotExist(institutionId, institution);
        this.apimClient.createInstitutionSubscription(
                institutionId,
                institution.getDescription(),
                subscriptionScope,
                subscriptionId,
                subscriptionName);

        List<InstitutionApiKeys> apiSubscriptions = this.apimClient.getApiSubscriptions(institutionId);

        if (subscriptionCode.getAuthDomain() != null) {
            AuthorizationConfig authorizationConfig = getAuthorizationConfig(subscriptionCode, institution);

            InstitutionApiKeys apiKeys = apiSubscriptions.stream()
                    .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                    .findFirst()
                    .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

            // configure primary key
            Authorization authorizationPrimaryKey = buildAuthorization(subscriptionCode, apiKeys.getPrimaryKey(), institution, true, authorizationConfig);
            this.authorizerConfigClient.createAuthorization(authorizationPrimaryKey);

            // configure secondary key
            Authorization authorizationSecondaryKey = buildAuthorization(subscriptionCode, apiKeys.getSecondaryKey(), institution, false, authorizationConfig);
            this.authorizerConfigClient.createAuthorization(authorizationSecondaryKey);
        }

        return InstitutionApiKeysResource.builder()
                .institutionApiKeys(apiSubscriptions)
                .build();
    }

    /**
     * Regenerate the primary subscription key to the specified subscription for the given institution.
     * <p>
     * If the subscription is for {@link Subscription#BO_EXT_EC} or {@link Subscription#BO_EXT_PSP} then it update
     * the Authorizer config service with the new api key.
     *
     * @param institutionId  the id of the institution
     * @param subscriptionId the id of the subscription
     */
    public void regeneratePrimaryKey(@NotNull String institutionId, @NotNull String subscriptionId) {
        InstitutionResponse institution = getInstitutionResponse(institutionId);
        var prefix = subscriptionId.split("-")[0] + "-";
        Subscription subscription = Subscription.fromPrefix(prefix);
        checkIfInstitutionCanOperateOnSubscriptionOtherwiseThrowException(institution, subscription);
        if (subscription.equals(Subscription.FDR_PSP) && !InstitutionType.PT.equals(institution.getInstitutionType())) {
            checkIfPSPCodeIsAvailableOtherwiseThrowException(institution, subscription);
        }
        this.apimClient.regeneratePrimaryKey(subscriptionId);

        if (subscription.getAuthDomain() != null) {
            updateAuthorization(institution, subscriptionId, subscription, true);
        }
    }

    /**
     * Regenerate the secondary subscription key to the specified subscription for the given institution.
     * <p>
     * If the subscription is for {@link Subscription#BO_EXT_EC} or {@link Subscription#BO_EXT_PSP} then it update
     * the Authorizer config service with the new api key.
     *
     * @param institutionId  the id of the institution
     * @param subscriptionId the id of the subscription
     */
    public void regenerateSecondaryKey(@NotNull String institutionId, @NotNull String subscriptionId) {
        InstitutionResponse institution = getInstitutionResponse(institutionId);
        var prefix = subscriptionId.split("-")[0] + "-";
        Subscription subscription = Subscription.fromPrefix(prefix);
        checkIfInstitutionCanOperateOnSubscriptionOtherwiseThrowException(institution, subscription);
        if (subscription.equals(Subscription.FDR_PSP) && !InstitutionType.PT.equals(institution.getInstitutionType())) {
            checkIfPSPCodeIsAvailableOtherwiseThrowException(institution, subscription);
        }
        this.apimClient.regenerateSecondaryKey(subscriptionId);

        if (subscription.getAuthDomain() != null) {
            updateAuthorization(institution, subscriptionId, subscription, false);
        }
    }

    /**
     * Updates authorizer config for all broker's API keys that require delegations configuration.
     * Retrieves the updated list of creditor institution and the relative list of segregation codes associated to
     * broker's station and use this list to update segregation codes metadata in authorizer configuration.
     *
     * @param institutionId broker's institution id
     * @param ciTaxCode     broker's tax code
     */
    public void updateBrokerAuthorizerSegregationCodesMetadata(
            String institutionId,
            String ciTaxCode
    ) {
        List<InstitutionApiKeys> apiKeys = this.apimClient.getApiSubscriptions(institutionId).parallelStream()
                .filter(this::checkIfAPIKeyHasAuthDelegations)
                .toList();

        CIStationSegregationCodesList ciSegregationCodes =
                this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(ciTaxCode);

        apiKeys.parallelStream().forEach(
                apiKey -> {
                    String prefixId = apiKey.getId().split("-")[0] + "-";
                    updateAuthorizerConfigMetadata(institutionId, prefixId, ciSegregationCodes, true, apiKey.getPrimaryKey());
                    updateAuthorizerConfigMetadata(institutionId, prefixId, ciSegregationCodes, false, apiKey.getSecondaryKey());
                }
        );
    }

    private void checkIfInstitutionCanOperateOnSubscriptionOtherwiseThrowException(
            InstitutionResponse institution,
            Subscription subscription
    ) {
        if (subscription.equals(Subscription.NODOAUTH)) {
            return;
        }

        RoleType institutionRoleType = fromSelfcareRole(institution.getTaxCode(), institution.getInstitutionType().name());
        if (institutionRoleType.equals(RoleType.PT)) {
            if (subscription.getAllowedInstitutionType().equals(RoleType.CI)) {
                try {
                    this.apiConfigClient.getBroker(institution.getTaxCode());
                } catch (FeignException.NotFound e) {
                    throw new AppException(AppError.INSTITUTION_TYPE_NOT_ALLOWED_ON_SUBSCRIPTION_BAD_REQUEST,
                            institution.getTaxCode(), institutionRoleType, subscription.getDisplayName());
                }
            } else {
                try {
                    this.apiConfigClient.getBrokerPsp(institution.getTaxCode());
                } catch (FeignException.NotFound e) {
                    throw new AppException(AppError.INSTITUTION_TYPE_NOT_ALLOWED_ON_SUBSCRIPTION_BAD_REQUEST,
                            institution.getTaxCode(), institutionRoleType, subscription.getDisplayName());
                }
            }
            return;
        }

        if (!subscription.getAllowedInstitutionType().equals(institutionRoleType)) {
            throw new AppException(AppError.INSTITUTION_TYPE_NOT_ALLOWED_ON_SUBSCRIPTION_BAD_REQUEST,
                    institution.getTaxCode(), institutionRoleType, subscription.getDisplayName());
        }
    }

    private boolean checkIfAPIKeyHasAuthDelegations(InstitutionApiKeys institutionApiKeys) {
        try {
            String prefixId = institutionApiKeys.getId().split("-")[0] + "-";
            return Subscription.fromPrefix(prefixId).isAuthDelegationConfigRequired();
        } catch (Exception e) {
            return false;
        }
    }

    private void updateAuthorization(
            InstitutionResponse institution,
            String subscriptionId,
            Subscription subscription,
            boolean isPrimaryKey
    ) {
        String institutionId = institution.getId();
        InstitutionApiKeys apiKeys = this.apimClient.getApiSubscriptions(institutionId).stream()
                .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

        AuthorizationConfig authorizationConfig = getAuthorizationConfig(subscription, institution);
        String subKey = isPrimaryKey ? apiKeys.getPrimaryKey() : apiKeys.getSecondaryKey();
        Authorization authorization;
        try {
            String authorizationId = createAuthorizationId(subscription.getPrefixId(), institutionId, isPrimaryKey);
            authorization = this.authorizerConfigClient.getAuthorization(authorizationId);

            authorization.setSubscriptionKey(subKey);
            authorization.setAuthorizedEntities(authorizationConfig.authorizationEntities);
            authorization.setOtherMetadata(
                    replaceExistingAuthorizationMetadataOtherwiseCreateNew(
                            authorizationConfig.authorizationMetadata,
                            authorization.getOtherMetadata())
            );

            this.authorizerConfigClient.deleteAuthorization(authorization.getId());
            this.authorizerConfigClient.createAuthorization(authorization);
        } catch (FeignException.NotFound e) {
            log.error("{} key authorizer configuration for institution {} and subscription {} not found, proceed to recreate the configuration",
                    isPrimaryKey ? PRIMARY : SECONDARY,
                    sanitizeLogParam(institutionId),
                    sanitizeLogParam(subscription.getDisplayName()),
                    e);
            authorization = buildAuthorization(subscription, subKey, institution, isPrimaryKey, authorizationConfig);
            this.authorizerConfigClient.createAuthorization(authorization);
        }
    }

    private List<AuthorizationMetadata> replaceExistingAuthorizationMetadataOtherwiseCreateNew(
            List<AuthorizationMetadata> authorizationMetadata,
            List<AuthorizationMetadata> existingMetadata
    ) {
        if (existingMetadata != null) {
            List<AuthorizationMetadata> newOtherMetadata = new ArrayList<>(existingMetadata);
            newOtherMetadata.removeIf(metadata -> metadata.getShortKey().equals(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY));
            newOtherMetadata.addAll(authorizationMetadata);
            return newOtherMetadata;
        }
        return authorizationMetadata;
    }

    private InstitutionResponse getInstitutionResponse(String institutionId) {
        it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution institution = this.externalApiClient.getInstitution(institutionId);
        if (institution == null) {
            throw new AppException(AppError.APIM_USER_NOT_FOUND, institutionId);
        }
        return modelMapper.map(institution, InstitutionResponse.class);
    }

    private Authorization buildAuthorization(
            Subscription subscription,
            String subscriptionKey,
            InstitutionResponse institution,
            boolean isPrimaryKey,
            AuthorizationConfig authorizationConfig
    ) {
        String authDomain = subscription.getAuthDomain();
        return Authorization.builder()
                .id(createAuthorizationId(subscription.getPrefixId(), institution.getId(), isPrimaryKey))
                .domain(authDomain)
                .subscriptionKey(subscriptionKey)
                .description(String.format("%s key configuration for %s", isPrimaryKey ? PRIMARY : SECONDARY, authDomain))
                .owner(AuthorizationOwner.builder()
                        .id(institution.getTaxCode())
                        .name(institution.getDescription())
                        .type(getOwnerType(institution))
                        .build())
                .authorizedEntities(authorizationConfig.authorizationEntities)
                .otherMetadata(authorizationConfig.authorizationMetadata)
                .build();
    }

    private List<AuthorizationEntity> getAuthorizationEntities(
            InstitutionResponse institution,
            Subscription subscription
    ) {
        List<AuthorizationEntity> authorizedEntities = new ArrayList<>();
       if (!InstitutionType.PT.equals(institution.getInstitutionType())) {
           authorizedEntities.add(getAuthorizationEntity(subscription, institution.getDescription(), institution.getTaxCode()));
       }

        List<DelegationExternal> delegationResponse = this.externalApiClient
                .getBrokerDelegation(null, institution.getId(), "prod-pagopa", "FULL", null)
                .parallelStream()
                .filter(delegation -> subscription.getAllowedInstitutionType().equals(fromSelfcareRole(delegation.getTaxCode(), delegation.getInstitutionType())))
                .toList();
        if (!delegationResponse.isEmpty()) {
            authorizedEntities.addAll(delegationResponse.parallelStream()
                    .map(elem -> getAuthorizationEntity(subscription, elem.getInstitutionName(), elem.getTaxCode()))
                    .filter(Objects::nonNull)
                    .toList());
        }

        return authorizedEntities;
    }

    private AuthorizationEntity getAuthorizationEntity(
            Subscription subscription,
            String institutionName,
            String taxCode
    ) {
        String value = taxCode;
        if (subscription.equals(Subscription.FDR_PSP)) {
            try {
                value = this.legacyPspCodeUtil.retrievePspCode(taxCode, false);
            } catch (Exception e) {
                log.warn("Error retrieving PSP code for institution with tax code {}, will not be included in API key authorizer configuration",
                        taxCode, e);
                return null;
            }
        }
        return AuthorizationEntity.builder()
                .name(institutionName)
                .value(value)
                .build();
    }

    private String getOwnerType(InstitutionResponse institution) {
        RoleType type = fromSelfcareRole(institution.getTaxCode(), institution.getInstitutionType().name());
        return RoleType.PT.equals(type) ? "BROKER" : type.name();
    }

    private String createAuthorizationId(String subscriptionPrefixId, String institutionId, boolean isPrimaryKey) {
        return String.format("%s%s_%s", subscriptionPrefixId, institutionId, isPrimaryKey ? PRIMARY : SECONDARY);
    }

    private void createUserIfNotExist(
            String institutionId,
            InstitutionResponse institution
    ) {
        try {
            this.apimClient.getInstitution(institutionId);
        } catch (IllegalArgumentException e) {
            // bad code, but it's needed to handle the creation of a new User on APIM
            CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
            dto.setDescription(institution.getDescription());
            dto.setTaxCode(institution.getTaxCode());
            dto.setEmail(!this.testEmail.isBlank() ? institutionId.concat(this.testEmail) : institution.getDigitalAddress());
            this.apimClient.createInstitution(institutionId, dto);
        }
    }

    private void updateAuthorizerConfigMetadata(
            String institutionId,
            String prefixId,
            CIStationSegregationCodesList ciSegregationCodes,
            boolean isPrimaryKey,
            String subKey
    ) {
        Subscription subscription = Subscription.fromPrefix(prefixId);
        Authorization authorization;
        String authorizationId = createAuthorizationId(prefixId, institutionId, isPrimaryKey);
        try {
            authorization = this.authorizerConfigClient.getAuthorization(authorizationId);
            authorization.setOtherMetadata(replaceExistingAuthorizationMetadataOtherwiseCreateNew(buildAuthorizationMetadata(ciSegregationCodes), authorization.getOtherMetadata()));
            this.authorizerConfigClient.updateAuthorization(authorizationId, authorization);
        } catch (FeignException.NotFound e) {
            log.error("{} key authorizer configuration for institution {} and subscription {} not found, proceed to recreate the configuration",
                    isPrimaryKey ? PRIMARY : SECONDARY,
                    sanitizeLogParam(institutionId),
                    sanitizeLogParam(subscription.getDisplayName()),
                    e);
            InstitutionResponse institution = getInstitutionResponse(institutionId);
            AuthorizationConfig authorizationConfig = getAuthorizationConfig(subscription, institution);
            authorization = buildAuthorization(subscription, subKey, institution, isPrimaryKey, authorizationConfig);
            this.authorizerConfigClient.createAuthorization(authorization);
        }
    }

    private List<AuthorizationMetadata> buildAuthorizationMetadata(CIStationSegregationCodesList codesList) {
        List<AuthorizationGenericKeyValue> genericKeyValues =
                codesList.getCiStationCodes().parallelStream()
                        .map(elem ->
                                AuthorizationGenericKeyValue.builder()
                                        .key(elem.getCiTaxCode())
                                        .values(elem.getSegregationCodes())
                                        .build()
                        )
                        .toList();

        return Collections.singletonList(AuthorizationMetadata.builder()
                .name("Segregation codes")
                .shortKey(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY)
                .content(genericKeyValues)
                .build());
    }

    private AuthorizationConfig getAuthorizationConfig(
            Subscription subscription,
            InstitutionResponse institution
    ) {
        List<AuthorizationMetadata> authorizationMetadata = new ArrayList<>();
        List<AuthorizationEntity> authorizationEntities = new ArrayList<>();

        if (subscription.isAuthDelegationConfigRequired()) {
            authorizationEntities = getAuthorizationEntities(institution, subscription);

            if (RoleType.CI.equals(subscription.getAllowedInstitutionType())) {
                CIStationSegregationCodesList ciSegregationCodes = this.apiConfigSelfcareIntegrationClient
                        .getCreditorInstitutionsSegregationCodeAssociatedToBroker(institution.getTaxCode());
                authorizationMetadata = buildAuthorizationMetadata(ciSegregationCodes);
            }
        }
        return new AuthorizationConfig(authorizationEntities, authorizationMetadata);
    }

    private String buildSubscriptionName(
            Subscription subscriptionCode,
            InstitutionResponse institution
    ) {
        String subscriptionName = String.format("%s %s", subscriptionCode.getDisplayName(), institution.getDescription());
        if (subscriptionName.length() >= 100) {
            return subscriptionName.substring(0, 99);
        }
        return subscriptionName;
    }

    private void checkIfPSPCodeIsAvailableOtherwiseThrowException(
            InstitutionResponse institution,
            Subscription subscriptionCode
    ) {
        try {
            this.legacyPspCodeUtil.retrievePspCode(institution.getTaxCode(), false);
        } catch (Exception e) {
            throw new AppException(AppError.PSP_APIM_KEY_ERROR, e, subscriptionCode.getDisplayName(), institution.getTaxCode());
        }
    }

    private char getEnvironment() {
        return environment.toLowerCase().charAt(0);
    }

    private record AuthorizationConfig(List<AuthorizationEntity> authorizationEntities,
                                       List<AuthorizationMetadata> authorizationMetadata) {

    }
}
