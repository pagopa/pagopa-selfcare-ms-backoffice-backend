package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AzureApiManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.*;
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
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiManagementService {

    protected static final String AN_INSTITUTION_ID_IS_REQUIRED = "An institutionId is required";

    private static final String SUBSCRIPTION_APIS_ID = "apis-";

    private static final String SUBSCRIPTION_APIS_DISPLAY = "Apis";

    @Autowired
    private AzureApiManagerClient apimClient;

    @Autowired
    private ExternalApiClient externalApiClient;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${institution.subscription.test-email}")
    private String testEmail;

    public List<InstitutionDetail> getInstitutions() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<InstitutionInfo> institutions = externalApiClient.getInstitutions(Utility.extractUserIdFromAuth(authentication));
        return institutions.stream()
                .map(institution -> modelMapper.map(institution, InstitutionDetail.class))
                .collect(Collectors.toList());
    }

    public Institution getInstitution(String institutionId) {
        return modelMapper.map(externalApiClient.getInstitution(institutionId), Institution.class);
    }

    public List<Product> getInstitutionProducts(String institutionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return externalApiClient.getInstitutionUserProducts(institutionId, Utility.extractUserIdFromAuth(authentication));
    }

    public List<Delegation> getBrokerDelegation(String institutionId, String brokerId) {
        return externalApiClient.getBrokerDelegation(institutionId, brokerId, "prod-pagopa", "FULL");
    }

    public List<InstitutionApiKeys> getInstitutionApiKeys(String institutionId) {
        return apimClient.getInstitutionApiKeys(institutionId);
    }

    public List<InstitutionApiKeys> createSubscriptionKeys(String institutionId, String subscriptionCode) {
        Subscription subscriptionEnum = Subscription.valueOf(subscriptionCode);
        String scope = subscriptionEnum.getScope();
        String subscriptionName = subscriptionEnum.getDisplayName();
        InstitutionResponse institution = externalApiClient.getInstitution(institutionId);
        if(institution != null) {
            String subscriptionId = subscriptionEnum.getPrefixId() + institution.getTaxCode();
            try {
                apimClient.createInstitutionSubscription(institutionId, institution.getDescription(), scope, subscriptionId, subscriptionName + " " + institution.getDescription());
            } catch (RuntimeException e) {
                CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
                dto.setDescription(institution.getDescription());
                dto.setTaxCode(institution.getTaxCode());
                dto.setEmail(!testEmail.isBlank() ? institutionId.concat(testEmail) : institution.getDigitalAddress());
                apimClient.createInstitution(institutionId, dto);
                apimClient.createInstitutionSubscription(institutionId, institution.getDescription(), scope, subscriptionId, subscriptionName);
            }
        } else {
            throw new ResourceNotFoundException(String.format("The institution %s was not found", institutionId));
        }
        return apimClient.getApiSubscriptions(institutionId);
    }

    public List<InstitutionApiKeys> createInstitutionKeys(String institutionId) {
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);

        InstitutionResponse institution = externalApiClient.getInstitution(institutionId);
        if(institution != null) {

            try {
                apimClient.createInstitutionSubscription(institutionId, institution.getDescription(), "/apis", SUBSCRIPTION_APIS_ID.concat(institutionId), SUBSCRIPTION_APIS_DISPLAY);
            } catch (RuntimeException e) {
                CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
                dto.setDescription(institution.getDescription());
                dto.setTaxCode(institution.getTaxCode());
                dto.setEmail(!testEmail.isBlank() ? institutionId.concat(testEmail) : institution.getDigitalAddress());
                apimClient.createInstitution(institutionId, dto);
                apimClient.createInstitutionSubscription(institutionId, institution.getDescription(), "/apis", SUBSCRIPTION_APIS_ID.concat(institutionId), SUBSCRIPTION_APIS_DISPLAY);
            }
        } else {
            throw new ResourceNotFoundException(String.format("The institution %s was not found", institutionId));
        }
        return apimClient.getApiSubscriptions(institutionId);
    }

    public void regeneratePrimaryKey(String subscriptionId) {
        apimClient.regeneratePrimaryKey(subscriptionId);
    }

    public void regenerateSecondaryKey(String subscriptionId) {
        apimClient.regenerateSecondaryKey(subscriptionId);
    }


}

