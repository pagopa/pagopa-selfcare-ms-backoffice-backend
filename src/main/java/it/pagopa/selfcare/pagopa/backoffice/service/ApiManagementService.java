package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationGenericKeyValue;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationMetadata;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationOwner;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionStationSegregationCodes;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionStationSegregationCodesList;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionApiKeysResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ProductResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Subscription;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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


    @Autowired
    public ApiManagementService(
            AzureApiManagerClient apimClient,
            ExternalApiClient externalApiClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            ModelMapper modelMapper,
            AuthorizerConfigClient authorizerConfigClient,
            FeatureManager featureManager,
            @Value("${institution.subscription.test-email}") String testEmail,
            @Value("${info.properties.environment}") String environment
    ) {
        this.apimClient = apimClient;
        this.externalApiClient = externalApiClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.modelMapper = modelMapper;
        this.testEmail = testEmail;
        this.environment = environment;
        this.authorizerConfigClient = authorizerConfigClient;
        this.featureManager = featureManager;
    }

//    public InstitutionDetailResource getInstitutions(String taxCode) {
//        List<InstitutionDetail> institutionDetails;
//        if (taxCode != null && !taxCode.isEmpty()) {
//            if (!featureManager.isEnabled("isOperator")) {
//                throw new AppException(AppError.UNAUTHORIZED);
//            }
//            institutionDetails = apiManagementComponent.getInstitutionDetailsForOperator(taxCode);
//        } else {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String userIdForAuth = Utility.extractUserIdFromAuth(authentication);
//            institutionDetails = apiManagementComponent.getInstitutionDetails(userIdForAuth);
//        }
//        return InstitutionDetailResource.builder()
//                .institutionDetails(institutionDetails)
//                .build();
//    }

    public InstitutionDetailResource getInstitutions(String taxCode) {
        List<InstitutionDetail> institutionDetails;
        if (taxCode != null && !taxCode.isEmpty()) {
            if (!featureManager.isEnabled("isOperator")) {
                throw new AppException(AppError.UNAUTHORIZED);
            }
            institutionDetails = externalApiClient.getInstitutionsFiltered(taxCode).getInstitutions().stream()
                    .map(elem -> modelMapper.map(elem, InstitutionDetail.class))
                    .toList();
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userIdForAuth = Utility.extractUserIdFromAuth(authentication);
            Collection<InstitutionInfo> institutions = externalApiClient.getInstitutions(userIdForAuth);
            institutionDetails = institutions.stream()
                    .map(institution -> modelMapper.map(institution, InstitutionDetail.class))
                    .toList();
        }
        return InstitutionDetailResource.builder()
                .institutionDetails(institutionDetails)
                .build();
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
                        RoleType roleType = RoleType.fromSelfcareRole(
                                delegation.getTaxCode(), delegation.getInstitutionType());
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

        String subscriptionId = String.format("%s%s", subscriptionCode.getPrefixId(), institution.getTaxCode());
        String subscriptionName = String.format("%s %s", subscriptionCode.getDisplayName(), institution.getDescription());
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
            DelegationInfo delegationInfoResponse = getDelegationInfo(institutionId, subscriptionCode.getAuthDelegations(), institution.getTaxCode());

            InstitutionApiKeys apiKeys = apiSubscriptions.stream()
                    .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                    .findFirst()
                    .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

            // configure primary key
            Authorization authorizationPrimaryKey = buildAuthorization(subscriptionCode, apiKeys.getPrimaryKey(), institution, true, delegationInfoResponse);
            this.authorizerConfigClient.createAuthorization(authorizationPrimaryKey);

            // configure secondary key
            Authorization authorizationSecondaryKey = buildAuthorization(subscriptionCode, apiKeys.getSecondaryKey(), institution, false, delegationInfoResponse);
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
        this.apimClient.regeneratePrimaryKey(subscriptionId);

        var prefix = subscriptionId.split("-")[0] + "-";
        if (Subscription.fromPrefix(prefix).getAuthDomain() != null) {
            updateAuthorization(institutionId, subscriptionId, prefix, true);
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
        this.apimClient.regenerateSecondaryKey(subscriptionId);

        var prefix = subscriptionId.split("-")[0] + "-";
        if (Subscription.fromPrefix(prefix).getAuthDomain() != null) {
            updateAuthorization(institutionId, subscriptionId, prefix, false);
        }
    }

    /**
     * Updates authorizer config for all broker's API keys that require delegations configuration.
     * Retrieves the updated list of creditor institution and the relative list of segregation codes associated to
     * broker's station and use this list to update authorizer configuration.
     *
     * @param institutionId broker's institution id
     * @param ciTaxCode     broker's tax code
     */
    public void updateBrokerAuthorizerSegregationCodesMetadata(
            String institutionId,
            String ciTaxCode
    ) {
        List<InstitutionApiKeys> apiKeys = this.apimClient.getApiSubscriptions(institutionId).stream()
                .filter(institutionApiKeys -> {
                    String prefixId = institutionApiKeys.getId().split("-")[0] + "-";
                    return Subscription.fromPrefix(prefixId).getAuthDelegations();
                }).toList();

        CreditorInstitutionStationSegregationCodesList ciSegregationCodes =
                this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionsSegregationCodeAssociatedToBroker(ciTaxCode);

        apiKeys.forEach(
                apiKey -> {
                    String prefixId = apiKey.getId().split("-")[0] + "-";
                    updateAuthorizerConfigMetadata(institutionId, prefixId, ciSegregationCodes, true);
                    updateAuthorizerConfigMetadata(institutionId, prefixId, ciSegregationCodes, false);
                }
        );
    }

    private void updateAuthorization(
            String institutionId,
            String subscriptionId,
            String subscriptionPrefixId,
            boolean isPrimaryKey
    ) {
        InstitutionApiKeys apiKeys = this.apimClient.getApiSubscriptions(institutionId).stream()
                .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

        Subscription subscription = Subscription.fromPrefix(subscriptionPrefixId);
        Authorization authorization;
        try {
            String authorizationId = createAuthorizationId(subscriptionPrefixId, institutionId, isPrimaryKey);
            authorization = this.authorizerConfigClient.getAuthorization(authorizationId);

            authorization.setSubscriptionKey(isPrimaryKey ? apiKeys.getPrimaryKey() : apiKeys.getSecondaryKey());

            InstitutionResponse institution = getInstitutionResponse(institutionId);
            DelegationInfo delegationInfoResponse = getDelegationInfo(institutionId, subscription.getAuthDelegations(), institution.getTaxCode());

            authorization.setAuthorizedEntities(getAuthorizationEntities(institution, delegationInfoResponse.delegationResponse));
            AuthorizationMetadata authorizationMetadata = buildAuthorizationMetadata(delegationInfoResponse.ciSegregationCodes.getCiStationCodes());
            if (authorization.getOtherMetadata() != null) {
                authorization.getOtherMetadata().removeIf(metadata -> metadata.getShortKey().equals(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY));
                authorization.getOtherMetadata().add(authorizationMetadata);
            } else {
                authorization.setOtherMetadata(Collections.singletonList(authorizationMetadata));
            }

            this.authorizerConfigClient.deleteAuthorization(authorization.getId());
            this.authorizerConfigClient.createAuthorization(authorization);
        } catch (FeignException.NotFound e) {
            log.error("An error occurred while updating API key authorizer configuration for institution {} and subscription {}, proceed to recreate the API key",
                    institutionId, subscription.getDisplayName(), e);
            createSubscriptionKeys(institutionId, subscription);
        }

    }

    private InstitutionResponse getInstitutionResponse(String institutionId) {
        InstitutionResponse institution = this.externalApiClient.getInstitution(institutionId);
        if (institution == null) {
            throw new AppException(AppError.APIM_USER_NOT_FOUND, institutionId);
        }
        return institution;
    }

    private Authorization buildAuthorization(
            Subscription subscription,
            String subscriptionKey,
            InstitutionResponse institution,
            boolean isPrimaryKey,
            DelegationInfo delegationInfo
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
                .authorizedEntities(getAuthorizationEntities(institution, delegationInfo.delegationResponse))
                .otherMetadata(Collections.singletonList(buildAuthorizationMetadata(delegationInfo.ciSegregationCodes.getCiStationCodes())))
                .build();
    }

    private List<AuthorizationEntity> getAuthorizationEntities(
            InstitutionResponse institution,
            List<DelegationExternal> delegationResponse
    ) {
        List<AuthorizationEntity> authorizedEntities = new ArrayList<>();

        if (delegationResponse != null && !delegationResponse.isEmpty()) {
            authorizedEntities = new ArrayList<>(delegationResponse.stream()
                    .map(elem -> AuthorizationEntity.builder()
                            .name(elem.getInstitutionName())
                            .value(elem.getTaxCode())
                            .build())
                    .toList());
        }
        authorizedEntities.add(AuthorizationEntity.builder()
                .name(institution.getDescription())
                .value(institution.getTaxCode())
                .build());
        return authorizedEntities;
    }

    private String getOwnerType(InstitutionResponse institution) {
        RoleType type = RoleType.fromSelfcareRole(institution.getTaxCode(), institution.getInstitutionType().name());
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

    private char getEnvironment() {
        return environment.toLowerCase().charAt(0);
    }

    private void updateAuthorizerConfigMetadata(
            String institutionId,
            String prefixId,
            CreditorInstitutionStationSegregationCodesList ciSegregationCodes,
            boolean isPrimaryKey
    ) {
        Subscription subscription = Subscription.fromPrefix(prefixId);
        try {
            String authorizationId = createAuthorizationId(prefixId, institutionId, isPrimaryKey);
            Authorization authorization = this.authorizerConfigClient.getAuthorization(authorizationId);

            authorization.getOtherMetadata().removeIf(metadata -> metadata.getShortKey().equals(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY));
            authorization.getOtherMetadata().add(buildAuthorizationMetadata(ciSegregationCodes.getCiStationCodes()));

            this.authorizerConfigClient.deleteAuthorization(authorization.getId());
            this.authorizerConfigClient.createAuthorization(authorization);
        } catch (FeignException.NotFound e) {
            log.error("An error occurred while updating API key authorizer configuration for institution {} and subscription {}, proceed to recreate the API key",
                    institutionId, subscription.getDisplayName(), e);
            createSubscriptionKeys(institutionId, subscription);
        }
    }

    private AuthorizationMetadata buildAuthorizationMetadata(List<CreditorInstitutionStationSegregationCodes> ciStationCodes) {
        List<AuthorizationGenericKeyValue> genericKeyValues =
                ciStationCodes.parallelStream()
                        .map(elem ->
                                AuthorizationGenericKeyValue.builder()
                                        .key(elem.getCiTaxCode())
                                        .values(elem.getSegregationCodes())
                                        .build()
                        )
                        .toList();

        return AuthorizationMetadata.builder()
                .name("Segregation codes")
                .shortKey(AUTHORIZER_SEGREGATION_CODES_METADATA_SHORT_KEY)
                .content(genericKeyValues)
                .build();
    }

    private DelegationInfo getDelegationInfo(
            String institutionId,
            Boolean hasAuthDelegations,
            String institutionTaxCode
    ) {
        List<DelegationExternal> delegationResponse = new ArrayList<>();
        CreditorInstitutionStationSegregationCodesList ciSegregationCodes = new CreditorInstitutionStationSegregationCodesList(new ArrayList<>());
        if (Boolean.TRUE.equals(hasAuthDelegations)) {
            delegationResponse = this.externalApiClient
                    .getBrokerDelegation(null, institutionId, "prod-pagopa", "FULL", null);
            ciSegregationCodes =
                    this.apiConfigSelfcareIntegrationClient
                            .getCreditorInstitutionsSegregationCodeAssociatedToBroker(institutionTaxCode);
        }
        return new DelegationInfo(delegationResponse, ciSegregationCodes);
    }

    private record DelegationInfo(List<DelegationExternal> delegationResponse,
                                  CreditorInstitutionStationSegregationCodesList ciSegregationCodes) {
    }
}

