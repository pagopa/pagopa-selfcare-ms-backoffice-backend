package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class ApiManagementServiceImpl implements ApiManagementService {

    protected static final String AN_INSTITUTION_ID_IS_REQUIRED = "An institutionId is required";
    private final ApiManagerConnector apiManagerConnector;
    private final ExternalApiConnector externalApiConnector;

    @Autowired
    public ApiManagementServiceImpl(ApiManagerConnector apiManagerConnector,
                                    ExternalApiConnector externalApiConnector) {
        this.apiManagerConnector = apiManagerConnector;
        this.externalApiConnector = externalApiConnector;
    }


    @Override
    public InstitutionApiKeys createInstitutionKeys(String institutionId) {
        log.trace("createInstitutionKeys start");
        log.debug("createInstitutionKeys institutionId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        InstitutionApiKeys apiKeys = null;
        Institution institution = externalApiConnector.getInstitution(institutionId);
        if (institution != null) {
            try {
                apiKeys = apiManagerConnector.createInstitutionSubscription(institutionId, institution.getDescription());
            } catch (RuntimeException e) {
                CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
                dto.setDescription(institution.getDescription());
                dto.setFiscalCode(institution.getTaxCode());
                dto.setEmail(institution.getDigitalAddress());
                apiManagerConnector.createInstitution(institutionId, dto);
                apiKeys = apiManagerConnector.createInstitutionSubscription(institutionId, institution.getDescription());
            }
        } else {
            throw new ResourceNotFoundException(String.format("The institution %s was not found", institutionId));
        }
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "createInstitutionKeys result = {}", apiKeys);
        log.trace("createInstitutionKeys end");
        return apiKeys;
    }

    @Override
    public InstitutionApiKeys getInstitutionApiKeys(String institutionId) throws ResourceNotFoundException {
        log.trace("getInstitutionApiKeys start");
        log.debug("getInstitutionApiKeys userId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        InstitutionApiKeys apiKeys = null;
        apiKeys = apiManagerConnector.getInstitutionApiKeys(institutionId);
        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getInstitutionApiKeys result = {}", apiKeys);
        log.trace("getInstitutionApiKeys end");
        return apiKeys;
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
