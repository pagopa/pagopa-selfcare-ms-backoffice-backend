package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.Response;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.Context;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.apimanagement.ApiManagementManager;
import com.azure.resourcemanager.apimanagement.models.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AzureApiManagerClient implements ApiManagerConnector {

    private final ApiManagementManager manager;
    private final String serviceName;
    private final String resourceGroupName;
    private final String sid;
    private final String subscriptionId;

    public AzureApiManagerClient(@Value("${azure.resource-manager.api-manager.service-name}") String serviceName,
                                 @Value("${azure.resource-manager.api-manager.resource-group}") String resourceGroupName,
                                 @Value("${azure.resource-manager.api-manager.subscription-id}") String subscriptionId,
                                 @Value("${azure.resource-manager.api-manager.sid}") String sid) {
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        this.manager = ApiManagementManager
                .authenticate(credential, profile);
        this.serviceName = serviceName;
        this.resourceGroupName = resourceGroupName;
        this.sid = sid;
        this.subscriptionId = subscriptionId;

    }

    private static final Function<SubscriptionContract, UserSubscription> SUBSCRIPTION_CONTRACT_TO_USER_SUBSCRIPTION_FUNCTION = subscriptionContract -> {
        UserSubscription subscription = new UserSubscription();
        subscription.setId(subscriptionContract.id());
        subscription.setName(subscriptionContract.name());
        return subscription;
    };


    @Override
    public void createInstitution(String userId, CreateInstitutionApiKeyDto dto) {
        UserContract userContract = manager
                .users()
                .define(userId)
                .withExistingService(resourceGroupName, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getFiscalCode())
                .withLastName(dto.getDescription())//TODO cosa metterci?
                .withConfirmation(Confirmation.SIGNUP)
                .create();
    }

    @Override
    public UserSubscription createInstitutionSubscription(String institutionId, String institutionName) {
        SubscriptionContract contract = manager.subscriptions().createOrUpdate(resourceGroupName,
                serviceName,
                sid,
                new SubscriptionCreateParameters()
                        .withOwnerId(String.format("users/%s", institutionId))
                        .withDisplayName(institutionName)
                        .withScope("/apis")
        );
        
        UserSubscription subscription = SUBSCRIPTION_CONTRACT_TO_USER_SUBSCRIPTION_FUNCTION.apply(contract);
        getApiKeys(subscription);
        return subscription;
    }

    @Override
    public UserSubscription getUserSubscription(String userId) {
        log.trace("getUser start");
        log.debug("getUser serviceName = {}, resourceGroup = {}, userId = {}", serviceName, resourceGroupName, userId);
        final Response<SubscriptionContract> response = manager.userSubscriptions().getWithResponse(resourceGroupName, serviceName, userId, sid, Context.NONE);
        UserSubscription subscription = null;
        if (response.getValue() != null) {
            subscription = SUBSCRIPTION_CONTRACT_TO_USER_SUBSCRIPTION_FUNCTION.apply(response.getValue());
            getApiKeys(subscription);
        }
        log.debug("getUser result = {}", subscription);
        return subscription;
    }

    private UserSubscription getApiKeys(UserSubscription subscription) {
        Response<SubscriptionKeysContract> subscriptionKeysContractResponse = manager.subscriptions().listSecretsWithResponse(resourceGroupName, serviceName, sid, Context.NONE);
        if (subscriptionKeysContractResponse.getValue() != null) {
            subscription.setPrimaryKey(subscriptionKeysContractResponse.getValue().primaryKey());
            subscription.setSecondaryKey(subscriptionKeysContractResponse.getValue().secondaryKey());
        }
        return subscription;
    }
}
