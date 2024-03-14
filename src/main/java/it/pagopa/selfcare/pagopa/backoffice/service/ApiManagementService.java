package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AuthorizerConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationList;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationOwner;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationOwnerType;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
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

    @Autowired
    public ApiManagementService(AzureApiManagerClient apimClient,
                                ExternalApiClient externalApiClient,
                                ModelMapper modelMapper,
                                @Value("${institution.subscription.test-email}") String testEmail,
                                @Value("${info.properties.environment}") String environment,
                                AuthorizerConfigClient authorizerConfigClient
    ) {
        this.apimClient = apimClient;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
        this.testEmail = testEmail;
        this.authorizerConfigClient = authorizerConfigClient;
        this.environment = environment;
    }

    public List<InstitutionDetail> getInstitutions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<InstitutionInfo> institutions = externalApiClient.getInstitutions(Utility.extractUserIdFromAuth(authentication));
        return institutions.stream()
                .map(institution -> modelMapper.map(institution, InstitutionDetail.class))
                .toList();
    }

    public Institution getInstitution(String institutionId) {
        return modelMapper.map(externalApiClient.getInstitution(institutionId), Institution.class);
    }

    public List<Product> getInstitutionProducts(String institutionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return externalApiClient.getInstitutionUserProducts(institutionId, Utility.extractUserIdFromAuth(authentication));
    }

    public List<Delegation> getBrokerDelegation(String institutionId, String brokerId, List<RoleType> roles) {
        var response = externalApiClient.getBrokerDelegation(institutionId, brokerId, "prod-pagopa", "FULL");

        var result = response.stream()
                .map(elem -> modelMapper.map(elem, Delegation.class))
                .toList();

        // filter by roles
        if (roles != null && !roles.isEmpty()) {
            result = result.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(delegation -> {
                        RoleType roleType = RoleType.fromSelfcareRole(delegation.getInstitutionType());
                        return roles.contains(roleType);
                    })
                    .toList();
        }
        return result;
    }

    public List<InstitutionApiKeys> getInstitutionApiKeys(String institutionId) {
        return apimClient.getInstitutionApiKeys(institutionId);
    }

    /**
     * Create the subscription's api keys to the specified subscription for the specified institution.
     * <p>
     * If the subscription is for {@link Subscription#BO_EXT} then it configure the Authorizer config service in order to enable
     * the authorization process.
     *
     * @param institutionId the id of the institution
     * @param subscriptionCode the code of the requested subscription
     * @return the list of all institution's subscription's api keys
     */
    public List<InstitutionApiKeys> createSubscriptionKeys(String institutionId, Subscription subscriptionCode) {
        InstitutionResponse institution = getInstitutionResponse(institutionId);

        String subscriptionId = String.format("%s%s", subscriptionCode.getPrefixId(), institution.getTaxCode());
        String subscriptionName = String.format("%s %s", subscriptionCode.getDisplayName(), institution.getDescription());
        createUserIfNotExist(institutionId, institution);
        this.apimClient.createInstitutionSubscription(
                institutionId,
                institution.getDescription(),
                String.format(subscriptionCode.getScope(), getEnvironment()),
                subscriptionId,
                subscriptionName);

        List<InstitutionApiKeys> apiSubscriptions = this.apimClient.getApiSubscriptions(institutionId);
        if (!subscriptionCode.equals(Subscription.BO_EXT)) {
            return apiSubscriptions;
        }

        InstitutionApiKeys apiKeys = apiSubscriptions.stream()
                .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

        this.authorizerConfigClient.createAuthorization(buildAuthorization(apiKeys.getPrimaryKey(), institution, true));
        this.authorizerConfigClient.createAuthorization(buildAuthorization(apiKeys.getSecondaryKey(), institution, false));
        return apiSubscriptions;
    }

    /**
     * Regenerate the primary subscription key to the specified subscription for the given institution.
     * <p>
     * If the subscription is for {@link Subscription#BO_EXT} then it update the Authorizer config service
     * with the new api key.
     *
     * @param institutionId the id of the institution
     * @param subscriptionId the id of the subscription
     */
    public void regeneratePrimaryKey(String institutionId, String subscriptionId) {
        this.apimClient.regeneratePrimaryKey(subscriptionId);

        if (subscriptionId.startsWith(Subscription.BO_EXT.getPrefixId())) {
            updateAuthorization(institutionId, subscriptionId, true);
        }
    }

    /**
     * Regenerate the secondary subscription key to the specified subscription for the given institution.
     * <p>
     * If the subscription is for {@link Subscription#BO_EXT} then it update the Authorizer config service
     * with the new api key.
     *
     * @param institutionId the id of the institution
     * @param subscriptionId the id of the subscription
     */
    public void regenerateSecondaryKey(String institutionId, String subscriptionId) {
        this.apimClient.regenerateSecondaryKey(subscriptionId);

        if (subscriptionId.startsWith(Subscription.BO_EXT.getPrefixId())) {
            updateAuthorization(institutionId, subscriptionId, false);
        }
    }

    private void updateAuthorization(String institutionId, String subscriptionId, boolean isPrimaryKey) {
        InstitutionResponse institution = getInstitutionResponse(institutionId);

        InstitutionApiKeys apiKeys = this.apimClient.getApiSubscriptions(institutionId).stream()
                .filter(institutionApiKeys -> institutionApiKeys.getId().equals(subscriptionId))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.APIM_KEY_NOT_FOUND, institutionId));

        AuthorizationList authorizationList = this.authorizerConfigClient.getAuthorization(createAuthorizationBOId(institution, isPrimaryKey));
        if (authorizationList.getAuthorizations().size() != 1) {
            throw new AppException(AppError.AUTHORIZATION_NOT_FOUND, institutionId);
        }
        Authorization authorization = authorizationList.getAuthorizations().get(0);
        authorization.setSubscriptionKey(isPrimaryKey ? apiKeys.getPrimaryKey() : apiKeys.getSecondaryKey());
        this.authorizerConfigClient.updateAuthorization(authorization.getId(), authorization);
    }

    private InstitutionResponse getInstitutionResponse(String institutionId) {
        InstitutionResponse institution = this.externalApiClient.getInstitution(institutionId);
        if (institution == null) {
            throw new AppException(AppError.APIM_USER_NOT_FOUND, institutionId);
        }
        return institution;
    }

    private Authorization buildAuthorization(String subscriptionKey, InstitutionResponse institution, boolean isPrimaryKey) {
        return Authorization.builder()
                .id(createAuthorizationBOId(institution, isPrimaryKey))
                .domain("backoffice_external")
                .subscriptionKey(subscriptionKey)
                .description(String.format("%s key configuration for backoffice external", isPrimaryKey ? PRIMARY : SECONDARY))
                .owner(AuthorizationOwner.builder()
                        .id(institution.getTaxCode())
                        .name(institution.getDescription())
                        .type(AuthorizationOwnerType.fromSelfcareRole(institution.getInstitutionType().name()))
                        .build())
                .authorizedEntities(Collections.singletonList(AuthorizationEntity.builder()
                        .name(institution.getDescription())
                        .value(institution.getTaxCode())
                        .values(null)
                        .build()))
                .otherMetadata(Collections.emptyList())
                .build();
    }

    private String createAuthorizationBOId(InstitutionResponse institution, boolean isPrimaryKey) {
        return String.format("%s%s_%s", Subscription.BO_EXT.getPrefixId(), institution.getId(), isPrimaryKey ? PRIMARY : SECONDARY);
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

