package it.pagopa.selfcare.pagopa.backoffice.client;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.exception.ManagementException;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.Context;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.apimanagement.ApiManagementManager;
import com.azure.resourcemanager.apimanagement.models.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import it.pagopa.selfcare.pagopa.backoffice.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AzureApiManagerClient {

    private final static Function<UserContract, it.pagopa.selfcare.pagopa.backoffice.model.connector.UserContract> AZURE_USER_CONTRACT_TO_PAGOPA_USER_CONTRACT = userContract -> {
        it.pagopa.selfcare.pagopa.backoffice.model.connector.UserContract contract = new it.pagopa.selfcare.pagopa.backoffice.model.connector.UserContract();
        contract.setId(userContract.id());
        contract.setEmail(userContract.email());
        contract.setName(userContract.lastName());
        contract.setFullName(userContract.name());
        contract.setTaxCode(userContract.firstName());
        return contract;
    };
    private final ApiManagementManager manager;
    private final String serviceName;
    private final String resourceGroupName;

    public AzureApiManagerClient(@Value("${azure.resource-manager.api-manager.service-name}") String serviceName,
                                 @Value("${azure.resource-manager.api-manager.resource-group}") String resourceGroupName,
                                 @Value("${azure.resource-manager.api-manager.subscription-id}") String subscriptionId,
                                 @Value("${azure.resource-manager.api-manager.tenant-id}") String tenantId) {
        this.serviceName = serviceName;
        this.resourceGroupName = resourceGroupName;

        AzureProfile profile = new AzureProfile(tenantId, subscriptionId, AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        this.manager = ApiManagementManager
                .authenticate(credential, profile);

    }

    public it.pagopa.selfcare.pagopa.backoffice.model.connector.UserContract createInstitution(String institutionId, CreateInstitutionApiKeyDto dto) {
        log.trace("createInstitution start");
        log.debug("createInstitution userId = {}, dto = {}", institutionId, dto);
        UserContract userContract = manager
                .users()
                .define(institutionId)
                .withExistingService(resourceGroupName, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getTaxCode())
                .withLastName(StringUtils.truncateString(StringUtils.validateAndReplace(dto.getDescription(), "-"), 100))
                .withConfirmation(Confirmation.SIGNUP)
                .create();
        it.pagopa.selfcare.pagopa.backoffice.model.connector.UserContract contract = AZURE_USER_CONTRACT_TO_PAGOPA_USER_CONTRACT.apply(userContract);
        log.debug(Constants.CONFIDENTIAL_MARKER, "createInstitution userContract = {}", userContract);
        log.trace("createInstitution end");
        return contract;
    }

    public List<InstitutionApiKeys> createInstitutionSubscription(String institutionId, String institutionName) {
        log.trace("createInstitutionSubscription start");
        log.debug("createInstitutionSubscription institutionId = {}, institutionName = {}", institutionId, institutionName);
        SubscriptionContract contract = manager.subscriptions().createOrUpdate(resourceGroupName,
                serviceName,
                institutionId,
                new SubscriptionCreateParameters()
                        .withOwnerId(String.format("/users/%s", institutionId))
                        .withDisplayName(institutionName)
                        .withScope("/apis")
        );

        List<InstitutionApiKeys> apiKeys = getApiSubscriptions(institutionId);
        log.debug(Constants.CONFIDENTIAL_MARKER, "createInstitutionSubscription apiKeys = {}", apiKeys);
        log.trace("createInstitutionSubscription end");
        return apiKeys;
    }

    public void createInstitutionSubscription(String institutionId, String institutionName, String scope, String subscriptionId, String subscriptionName) {
        log.trace("createInstitutionSubscription start");
        log.debug("createInstitutionSubscription institutionId = {}, institutionName = {}", institutionId, institutionName);
        manager.subscriptions().createOrUpdate(resourceGroupName,
                serviceName,
                subscriptionId,
                new SubscriptionCreateParameters()
                        .withOwnerId(String.format("/users/%s", institutionId))
                        .withDisplayName(subscriptionName)
                        .withScope(scope)
        );
        log.trace("createInstitutionSubscription end");
    }

    public List<InstitutionApiKeys> getInstitutionApiKeys(String institutionId) {
        log.trace("getInstitutionApiKeys start");
        log.debug("getInstitutionApiKeys serviceName = {}, resourceGroup = {}, institutionId = {}", serviceName, resourceGroupName, institutionId);
        List<InstitutionApiKeys> subscriptions = getApiSubscriptions(institutionId);
        log.debug(Constants.CONFIDENTIAL_MARKER, "getUser result = {}", subscriptions);
        return subscriptions;
    }

    public void regeneratePrimaryKey(String institutionId) {
        log.trace("regeneratePrimaryKey start");
        log.debug("regeneratePrimaryKey institutionId = {}", institutionId);
        manager.subscriptions().regeneratePrimaryKey(resourceGroupName, serviceName, institutionId);
        log.trace("regeneratePrimaryKey end");
    }

    public void regenerateSecondaryKey(String institutionId) {
        log.trace("regenerateSecondaryKey start");
        log.debug("regenerateSecondaryKey institutionId = {}", institutionId);
        manager.subscriptions().regenerateSecondaryKey(resourceGroupName, serviceName, institutionId);
        log.trace("regenerateSecondaryKey end");
    }


    public List<InstitutionApiKeys> getApiSubscriptions(String institutionId) {
        log.trace("getApiSubscriptions start");
        log.debug("getApiSubscriptions institutionId = {}", institutionId);

        PagedIterable<SubscriptionContract> subscriptionContractList = null;
        List<InstitutionApiKeys> institutionApiKeysList = null;
        try {
            subscriptionContractList = manager.userSubscriptions().list(resourceGroupName, serviceName, institutionId);

            institutionApiKeysList = subscriptionContractList.stream()
                    .map(contract -> {
                        InstitutionApiKeys apiKeys = new InstitutionApiKeys();
                        Response<SubscriptionKeysContract> response = manager.subscriptions().listSecretsWithResponse(resourceGroupName, serviceName, contract.name(), Context.NONE);
                        if(response.getValue() != null) {
                            apiKeys = new InstitutionApiKeys();
                            apiKeys.setPrimaryKey(response.getValue().primaryKey());
                            apiKeys.setSecondaryKey(response.getValue().secondaryKey());
                            apiKeys.setDisplayName(contract.displayName());
                            apiKeys.setId(contract.name());
                        }
                        return apiKeys;
                    }).collect(Collectors.toList());
        } catch (ManagementException e) {
            institutionApiKeysList = new ArrayList<>();
        }
        log.debug(Constants.CONFIDENTIAL_MARKER, "getApiSubscriptions response = {}", subscriptionContractList);
        log.trace("getApiSubscriptions end");

        return institutionApiKeysList;
    }
}
