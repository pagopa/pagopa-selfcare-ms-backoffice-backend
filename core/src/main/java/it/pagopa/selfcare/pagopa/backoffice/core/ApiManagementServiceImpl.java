package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
public class ApiManagementServiceImpl implements ApiManagementService {

    protected static final String AN_INSTITUTION_ID_IS_REQUIRED = "An institutionId is required";
    private final ApiManagerConnector apiManagerConnector;
    private final ExternalApiConnector externalApiConnector;
    private final String testEmail;

    @Autowired
    public ApiManagementServiceImpl(@Value("${institution.subscription.test-email}") String testEmail,
                                    ApiManagerConnector apiManagerConnector,
                                    ExternalApiConnector externalApiConnector) {
        this.testEmail = testEmail;
        this.apiManagerConnector = apiManagerConnector;
        this.externalApiConnector = externalApiConnector;
    }


    @Override
    public List<InstitutionApiKeys> createInstitutionKeys(String institutionId) {
        log.trace("createInstitutionKeys start");
        log.debug("createInstitutionKeys institutionId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);

        Institution institution = externalApiConnector.getInstitution(institutionId);
        if (institution != null) {

            try {
                apiManagerConnector.createInstitutionSubscription(institutionId, institution.getDescription(), "/apis", "subscriptionApis", "subscriptionName");
//                apiKeys = apiManagerConnector.createInstitutionSubscription(institutionId, institution.getDescription());
            } catch (RuntimeException e) {
                CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
                dto.setDescription(institution.getDescription());
                dto.setFiscalCode(institution.getTaxCode());
                if (!testEmail.isBlank())
                    dto.setEmail(testEmail);
                else
                    dto.setEmail(institution.getDigitalAddress());
                apiManagerConnector.createInstitution(institutionId, dto);
                apiManagerConnector.createInstitutionSubscription(institutionId, institution.getDescription(), "/apis", "subscriptionApis", "subscriptionName");

            }
        } else {
            throw new ResourceNotFoundException(String.format("The institution %s was not found", institutionId));
        }
        List<InstitutionApiKeys> apiSubscriptionsList = apiManagerConnector.getApiSubscriptions(institutionId);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createInstitutionKeys result = {}", apiSubscriptionsList);
        log.trace("createInstitutionKeys end");
        return apiSubscriptionsList;
    }

    @Override
    public List<InstitutionApiKeys> createInstitutionKeysList(String institutionId) {
        log.trace("createInstitutionKeys start");
        log.debug("createInstitutionKeys for product {}, {} and {}", "nodo-auth", "bizevents", "debt-positions");
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);

        createInstitutionKeys(institutionId, "/products/nodo-auth", "subscriptionProductNodoAuth", "Nodo Auth");
        createInstitutionKeys(institutionId, "/products/bizevents", "subscriptionProductGpd", "Gestione Posizione Debitoria");
        createInstitutionKeys(institutionId, "/products/debt-positions", "subscriptionProductBeS", "Biz event service");
        List<InstitutionApiKeys> apiSubscriptionsList = apiManagerConnector.getApiSubscriptions(institutionId);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createInstitutionKeys result = {}", apiSubscriptionsList);/////
        log.trace("createInstitutionKeys end");
        return apiSubscriptionsList;
    }

    private void createInstitutionKeys(String institutionId, String scope, String subscriptionId, String subscriptionName) {
        Institution institution = externalApiConnector.getInstitution(institutionId);
        if (institution != null) {
            try {
                apiManagerConnector.createInstitutionSubscription(institutionId, institution.getDescription(), scope, subscriptionId, subscriptionName);
//
            } catch (RuntimeException e) {
                CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
                dto.setDescription(institution.getDescription());
                dto.setFiscalCode(institution.getTaxCode());
                if (!testEmail.isBlank())
                    dto.setEmail(testEmail);
                else
                    dto.setEmail(institution.getDigitalAddress());
                apiManagerConnector.createInstitution(institutionId, dto);
                apiManagerConnector.createInstitutionSubscription(institutionId, institution.getDescription(), scope, subscriptionId, subscriptionName);
            }
        } else {
            throw new ResourceNotFoundException(String.format("The institution %s was not found", institutionId));
        }
    }

    @Override
    public List<InstitutionApiKeys> getInstitutionApiKeys(String institutionId) throws ResourceNotFoundException {
        log.trace("getInstitutionApiKeys start");
        log.debug("getInstitutionApiKeys userId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        List<InstitutionApiKeys> apiKeysList = null;
        apiKeysList = apiManagerConnector.getInstitutionApiKeys(institutionId);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getInstitutionApiKeys result = {}", apiKeysList);
        log.trace("getInstitutionApiKeys end");
        return apiKeysList;
    }

    @Override
    public void regeneratePrimaryKey(String institutionId) {
        log.trace("regeneratePrimaryKey start");
        log.debug("regeneratePrimaryKey userId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        apiManagerConnector.regeneratePrimaryKey(institutionId);
        log.trace("regeneratePrimaryKey end");
    }

    @Override
    public void regenerateSecondaryKey(String institutionId) {
        log.trace("regenerateSecondaryKey start");
        log.debug("regenerateSecondaryKey userId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        apiManagerConnector.regenerateSecondaryKey(institutionId);
        log.trace("regenerateSecondaryKey end");
    }

}
