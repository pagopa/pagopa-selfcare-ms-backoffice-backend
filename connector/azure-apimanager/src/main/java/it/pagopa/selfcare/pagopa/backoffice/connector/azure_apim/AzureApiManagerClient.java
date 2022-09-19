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
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
public class AzureApiManagerClient implements ApiManagerConnector {

    private final ApiManagementManager manager;
    private final String serviceName;
    private final String resourceGroupName;


    public AzureApiManagerClient(@Value("${azure.resource-manager.api-manager.service-name}") String serviceName,
                                 @Value("${azure.resource-manager.api-manager.resource-group}") String resourceGroupName,
                                 @Value("${azure.resource-manager.api-manager.subscription-id}") String subscriptionId,
                                 @Value("${azure.resource-manager.api-manager.tenant-id}") String tenantId
                                 ) {
        this.serviceName = serviceName;
        this.resourceGroupName = resourceGroupName;

        AzureProfile profile = new AzureProfile(tenantId, subscriptionId, AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        this.manager = ApiManagementManager
                .authenticate(credential, profile);

    }

    private final static Function<UserContract, it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract> AZURE_USER_CONTRACT_TO_PAGOPA_USER_CONTRACT = userContract -> {
        it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract contract = new it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract();
        contract.setId(userContract.id());
        contract.setEmail(userContract.email());
        contract.setName(userContract.lastName());
        contract.setFullName(userContract.name());
        contract.setTaxCode(userContract.firstName());
        return contract;
    };
    
    
    @Override
    public it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract createInstitution(String institutionId, CreateInstitutionApiKeyDto dto) {
        log.trace("createInstitution start");
        log.debug("createInstitution userId = {}, dto = {}", institutionId, dto);
        UserContract userContract = manager
                .users()
                .define(institutionId)
                .withExistingService(resourceGroupName, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getFiscalCode())
                .withLastName(dto.getDescription())
                .withConfirmation(Confirmation.SIGNUP)
                .create();
        it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract contract = AZURE_USER_CONTRACT_TO_PAGOPA_USER_CONTRACT.apply(userContract);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createInstitution userContract = {}", userContract);
        log.trace("createInstitution end");
        return contract;
    }

    @Override
    public InstitutionApiKeys createInstitutionSubscription(String institutionId, String institutionName) {
        log.trace("createInstitutionSubscription start");
        System.out.printf("createInstitutionSubscription institutionId = {}, institutionName = {}", institutionId, institutionName);
        SubscriptionContract contract = manager.subscriptions().createOrUpdate(resourceGroupName,
                serviceName,
                institutionId,
                new SubscriptionCreateParameters()
                        .withOwnerId(String.format("/users/%s", institutionId))
                        .withDisplayName(institutionName)
                        .withScope("/apis")
        );

        InstitutionApiKeys apiKeys = getApiKeys(institutionId);
        log.debug(LogUtils.CONFIDENTIAL_MARKER,"createInstitutionSubscription apiKeys = {}", apiKeys);
        log.trace("createInstitutionSubscription end");
        return apiKeys;
    }

    @Override
    public InstitutionApiKeys getInstitutionApiKeys(String institutionId) {
        log.trace("getInstitutionApiKeys start");
        log.debug("getInstitutionApiKeys serviceName = {}, resourceGroup = {}, institutionId = {}", serviceName, resourceGroupName, institutionId);
        InstitutionApiKeys subscription = getApiKeys(institutionId);
        log.debug(LogUtils.CONFIDENTIAL_MARKER,"getUser result = {}", subscription);
        return subscription;
    }

    @Override
    public void regeneratePrimaryKey(String institutionId) {
        log.trace("regeneratePrimaryKey start");
        log.debug("regeneratePrimaryKey institutionId = {}", institutionId);
        manager.subscriptions().regeneratePrimaryKey(resourceGroupName, serviceName, institutionId);
        log.trace("regeneratePrimaryKey end");
    }

    @Override
    public void regenerateSecondaryKey(String institutionId) {
        log.trace("regenerateSecondaryKey start");
        log.debug("regenerateSecondaryKey institutionId = {}", institutionId);
        manager.subscriptions().regenerateSecondaryKey(resourceGroupName, serviceName, institutionId);
        log.trace("regenerateSecondaryKey end");
    }


    private InstitutionApiKeys getApiKeys(String institutionId) {
        log.trace("getApiKeys start");
        log.debug("getApiKeys institutionId = {}", institutionId);
        InstitutionApiKeys apiKeys = null;
        Response<SubscriptionKeysContract> response = manager.subscriptions().listSecretsWithResponse(resourceGroupName, serviceName, institutionId, Context.NONE);
        if (response.getValue() != null) {
            apiKeys = new InstitutionApiKeys();
            apiKeys.setPrimaryKey(response.getValue().primaryKey());
            apiKeys.setSecondaryKey(response.getValue().secondaryKey());
        }
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getApiKeys response = {}", response);
        log.trace("getApiKeys end");
        return apiKeys;
    }
}
