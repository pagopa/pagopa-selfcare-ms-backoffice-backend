package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.Response;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.Context;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.apimanagement.ApiManagementManager;
import com.azure.resourcemanager.apimanagement.models.Confirmation;
import com.azure.resourcemanager.apimanagement.models.SubscriptionContract;
import com.azure.resourcemanager.apimanagement.models.SubscriptionKeysContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateSubscriptionDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("AzureAPIManagement")
public class AzureApimClient implements ApiManagerConnector {

    private final ApiManagementManager manager;
    private final String serviceName;
    private final String resourceGroupName;
    private final String sid;
    private final String subscriptionId;

    AzureApimClient(@Value("${azure.resource-manager.api-manager.service-name}")String serviceName,
                    @Value("${azure.resource-manager.api-manager.resource-group}")String resourceGroupName,
                    @Value("${azure.resource-manager.api-manager.subscription-id}")String subscriptionId,
                    @Value("${azure.resource-manager.api-manager.sid}")String sid) {
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        this.manager = ApiManagementManager
                .authenticate(credential, profile);
        this.serviceName = serviceName;
        this.resourceGroupName=resourceGroupName;
        this.sid = sid;
        this.subscriptionId = subscriptionId;
                
    }
    
    private static final Function<Response<SubscriptionContract>, UserSubscription> SUBSCRIPTION_CONTRACT_TO_USER_SUBSCRIPTION_FUNCTION = subscriptionContract -> {
        UserSubscription subscription = new UserSubscription();
        subscription.setId(subscriptionContract.getValue().id());  
        subscription.setPrimaryKey(subscriptionContract.getValue().primaryKey());
        return subscription;
    };
    


    @Override
    public void createSubscription(String userId, CreateSubscriptionDto dto) {
        manager
                .users()
                .define(userId)
                .withExistingService(resourceGroupName, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getDescription())
                .withLastName(dto.getExternalId())
                .withConfirmation(Confirmation.SIGNUP)
                .create();
    }

    @Override
    public UserSubscription getUserSubscription(String userId) {
        log.trace("getUser start");
        log.debug("getUser serviceName = {}, resourceGroup = {}, userId = {}", serviceName, resourceGroupName, userId);
        final Response<SubscriptionContract> response = manager.userSubscriptions().getWithResponse(resourceGroupName, serviceName, userId, sid, Context.NONE);
        Response<SubscriptionKeysContract> subscriptionKeysContractResponse = manager.subscriptions().listSecretsWithResponse(resourceGroupName, serviceName, sid, Context.NONE);
        UserSubscription subscription = null;
        if (response.getValue()!=null && subscriptionKeysContractResponse.getValue() != null){
             subscription = SUBSCRIPTION_CONTRACT_TO_USER_SUBSCRIPTION_FUNCTION.apply(response);
             subscription.setPrimaryKey(subscriptionKeysContractResponse.getValue().primaryKey());
             subscription.setSecondaryKey(subscriptionKeysContractResponse.getValue().secondaryKey());
        }
        log.debug("getUser result = {}", subscription);
        return subscription;
    }
}
