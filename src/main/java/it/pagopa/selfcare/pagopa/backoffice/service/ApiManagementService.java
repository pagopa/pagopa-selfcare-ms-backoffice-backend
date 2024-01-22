package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class ApiManagementService {

    private final AzureApiManagerClient apimClient;

    private final ExternalApiClient externalApiClient;

    private final ModelMapper modelMapper;

    @Value("${institution.subscription.test-email}")
    private String testEmail;

    public ApiManagementService(AzureApiManagerClient apimClient, ExternalApiClient externalApiClient, ModelMapper modelMapper) {
        this.apimClient = apimClient;
        this.externalApiClient = externalApiClient;
        this.modelMapper = modelMapper;
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

    public List<Delegation> getBrokerDelegation(String institutionId, String brokerId) {
        var response = externalApiClient.getBrokerDelegation(institutionId, brokerId, "prod-pagopa", "FULL");
        return response.stream()
                .map(elem -> modelMapper.map(elem, Delegation.class))
                .toList();
    }

    public List<InstitutionApiKeys> getInstitutionApiKeys(String institutionId) {
        return apimClient.getInstitutionApiKeys(institutionId);
    }

    public List<InstitutionApiKeys> createSubscriptionKeys(String institutionId, Subscription subscriptionCode) {
        String scope = subscriptionCode.getScope();
        InstitutionResponse institution = externalApiClient.getInstitution(institutionId);
        if(institution != null) {
            String subscriptionId = subscriptionCode.getPrefixId() + institution.getTaxCode();
            CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
            dto.setDescription(institution.getDescription());
            dto.setTaxCode(institution.getTaxCode());
            dto.setEmail(!testEmail.isBlank() ? institutionId.concat(testEmail) : institution.getDigitalAddress());
            String subscriptionName = subscriptionCode.getDisplayName() + " " + institution.getDescription();
            createUserIfNotExist(institutionId, dto);
            apimClient.createInstitutionSubscription(institutionId, institution.getDescription(), scope, subscriptionId, subscriptionName);
        } else {
            throw new AppException(AppError.APIM_USER_NOT_FOUND, institutionId);
        }
        return apimClient.getApiSubscriptions(institutionId);
    }

    private void createUserIfNotExist(String institutionId, CreateInstitutionApiKeyDto dto) {
        try {
            apimClient.getInstitution(institutionId);
        } catch (IllegalArgumentException e) {
            // bad code, but it's needed to handle the creation of a new User on APIM
            apimClient.createInstitution(institutionId, dto);
        }
    }

    public void regeneratePrimaryKey(String subscriptionId) {
        apimClient.regeneratePrimaryKey(subscriptionId);
    }

    public void regenerateSecondaryKey(String subscriptionId) {
        apimClient.regenerateSecondaryKey(subscriptionId);
    }


}

