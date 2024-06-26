package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationOwner;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetailResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ProductResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Subscription;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionApiKeysResource;
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
    private final AzureApiManagerClient apimClient;

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    private final String testEmail;

    private final String environment;

    private final AuthorizerConfigClient authorizerConfigClient;

    private final FeatureManager featureManager;

    @Autowired
    public ApiManagementService(AzureApiManagerClient apimClient,
                                ExternalApiClient externalApiClient,
                                ModelMapper modelMapper,
                                AuthorizerConfigClient authorizerConfigClient,
                                FeatureManager featureManager,
                                @Value("${institution.subscription.test-email}") String testEmail,
                                @Value("${info.properties.environment}") String environment
    ) {
        this.apimClient = apimClient;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
        this.testEmail = testEmail;
        this.environment = environment;
        this.authorizerConfigClient = authorizerConfigClient;
        this.featureManager = featureManager;
    }

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
        List<Product> institutionUserProducts = externalApiClient.getInstitutionUserProducts(institutionId, Utility.extractUserIdFromAuth(authentication));
        return ProductResource.builder()
                .products(institutionUserProducts)
                .build();
    }

    public DelegationResource getBrokerDelegation(String institutionId, String brokerId, List<RoleType> roles) {
        var response = externalApiClient.getBrokerDelegation(institutionId, brokerId, "prod-pagopa", "FULL", null);

        var result = response.stream()
                .map(elem -> modelMapper.map(elem, Delegation.class))
                .toList();

        // filter by roles
        if (roles != null && !roles.isEmpty()) {
            result = result.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(delegation -> {
                        log.info(delegation.toString());
                        RoleType roleType = RoleType.fromSelfcareRole(delegation.getTaxCode(), delegation.getInstitutionType());
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

        if (subscriptionCode == Subscription.BO_EXT_EC || subscriptionCode == Subscription.BO_EXT_PSP) {
            InstitutionApiKeys apiKeys = apiSubscriptions.stream()
                    .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                    .findFirst()
                    .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

            // configure primary key
            Authorization authorizationPrimaryKey = buildBOAuthorization(subscriptionCode.getPrefixId(), apiKeys.getPrimaryKey(), institution, true);
            this.authorizerConfigClient.createAuthorization(authorizationPrimaryKey);

            // configure secondary key
            Authorization authorizationSecondaryKey = buildBOAuthorization(subscriptionCode.getPrefixId(), apiKeys.getSecondaryKey(), institution, false);
            this.authorizerConfigClient.createAuthorization(authorizationSecondaryKey);
        }
        if (subscriptionCode == Subscription.FDR_ORG || subscriptionCode == Subscription.FDR_PSP) {
            InstitutionApiKeys apiKeys = apiSubscriptions.stream()
                    .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                    .findFirst()
                    .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));
            List<DelegationExternal> delegationResponse = this.externalApiClient.getBrokerDelegation(null, institutionId, "prod-pagopa", "FULL", null);

            // configure primary key
            Authorization authorizationPrimaryKey = buildFdrAuthorization(subscriptionCode.getPrefixId(), apiKeys.getPrimaryKey(), institution, delegationResponse, true, subscriptionCode);
            this.authorizerConfigClient.createAuthorization(authorizationPrimaryKey);

            // configure secondary key
            Authorization authorizationSecondaryKey = buildFdrAuthorization(subscriptionCode.getPrefixId(), apiKeys.getSecondaryKey(), institution, delegationResponse, false, subscriptionCode);
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
        if (prefix.equals(Subscription.BO_EXT_EC.getPrefixId()) || prefix.equals(Subscription.BO_EXT_PSP.getPrefixId()) // BO
                || prefix.equals(Subscription.FDR_ORG.getPrefixId()) || prefix.equals(Subscription.FDR_PSP.getPrefixId()) // Fdr
        ) {
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
        if (prefix.equals(Subscription.BO_EXT_EC.getPrefixId()) || prefix.equals(Subscription.BO_EXT_PSP.getPrefixId()) // BO
                || prefix.equals(Subscription.FDR_ORG.getPrefixId()) || prefix.equals(Subscription.FDR_PSP.getPrefixId()) // Fdr
        ) {
            updateAuthorization(institutionId, subscriptionId, prefix, false);
        }
    }

    private void updateAuthorization(String institutionId, String subscriptionId, String subscriptionPrefixId, boolean isPrimaryKey) {
        InstitutionApiKeys apiKeys = this.apimClient.getApiSubscriptions(institutionId).stream()
                .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

        String authorizationId = createAuthorizationBOId(subscriptionPrefixId, institutionId, isPrimaryKey);
        Authorization authorization = this.authorizerConfigClient.getAuthorization(authorizationId);
        if (authorization == null) {
            throw new AppException(AppError.AUTHORIZATION_NOT_FOUND, institutionId);
        }

        this.authorizerConfigClient.deleteAuthorization(authorization.getId());
        authorization.setSubscriptionKey(isPrimaryKey ? apiKeys.getPrimaryKey() : apiKeys.getSecondaryKey());
        this.authorizerConfigClient.createAuthorization(authorization);
    }

    private InstitutionResponse getInstitutionResponse(String institutionId) {
        InstitutionResponse institution = this.externalApiClient.getInstitution(institutionId);
        if (institution == null) {
            throw new AppException(AppError.APIM_USER_NOT_FOUND, institutionId);
        }
        return institution;
    }

    private Authorization buildBOAuthorization(
            String subscriptionPrefixId,
            String subscriptionKey,
            InstitutionResponse institution,
            boolean isPrimaryKey
    ) {
        log.info(institution.toString());
        return Authorization.builder()
                .id(createAuthorizationBOId(subscriptionPrefixId, institution.getId(), isPrimaryKey))
                .domain("backoffice_external")
                .subscriptionKey(subscriptionKey)
                .description(String.format("%s key configuration for backoffice external", isPrimaryKey ? PRIMARY : SECONDARY))
                .owner(AuthorizationOwner.builder()
                        .id(institution.getTaxCode())
                        .name(institution.getDescription())
                        .type(RoleType.fromSelfcareRole(institution.getTaxCode(), institution.getInstitutionType().name()))
                        .build())
                .authorizedEntities(Collections.singletonList(AuthorizationEntity.builder()
                        .name(institution.getDescription())
                        .value(institution.getTaxCode())
                        .values(null)
                        .build()))
                .otherMetadata(Collections.emptyList())
                .build();
    }

    private Authorization buildFdrAuthorization(
            String subscriptionPrefixId,
            String subscriptionKey,
            InstitutionResponse institution,
            List<DelegationExternal> delegationResponse,
            boolean isPrimaryKey,
            Subscription subscriptionCode) {

        ArrayList<AuthorizationEntity> authorizedEntities = new ArrayList<>(delegationResponse.stream()
                .map(elem -> AuthorizationEntity.builder()
                        .name(elem.getInstitutionName())
                        .value(elem.getTaxCode())
                        .build())
                .toList());
        authorizedEntities.add(AuthorizationEntity.builder()
                .name(institution.getDescription())
                .value(institution.getTaxCode())
                .build());
        log.info(institution.toString());
        return Authorization.builder()
                .id(createAuthorizationBOId(subscriptionPrefixId, institution.getId(), isPrimaryKey))
                .domain("fdr")
                .subscriptionKey(subscriptionKey)
                .description(String.format("%s key configuration for %s", isPrimaryKey ? PRIMARY : SECONDARY, subscriptionCode.name()))
                .owner(AuthorizationOwner.builder()
                        .id(institution.getTaxCode())
                        .name(institution.getDescription())
                        .type(RoleType.fromSelfcareRole(institution.getTaxCode(), institution.getInstitutionType().name()))
                        .build())
                .authorizedEntities(authorizedEntities)
                .otherMetadata(Collections.emptyList())
                .build();
    }

    private String createAuthorizationBOId(String subscriptionPrefixId, String institutionId, boolean isPrimaryKey) {
        return String.format("%s%s_%s", subscriptionPrefixId, institutionId, isPrimaryKey ? PRIMARY : SECONDARY);
    }

    private void createUserIfNotExist(String institutionId, InstitutionResponse institution) {
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
}

