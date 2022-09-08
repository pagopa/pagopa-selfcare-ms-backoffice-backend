package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiManagementServiceImpl implements ApiManagementService {

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
        log.trace("createUserSubscription start");
        log.debug("createUserSubscription institutionId = {}", institutionId);
        InstitutionApiKeys subscription = null;
        Institution institution = externalApiConnector.getInstitution(institutionId);
        try {
            subscription = apiManagerConnector.createInstitutionSubscription(institutionId,institution.getDescription());
        } catch (RuntimeException e) {
            CreateInstitutionApiKeyDto dto = new CreateInstitutionApiKeyDto();
            dto.setDescription(institution.getDescription());
            dto.setFiscalCode(institution.getTaxCode());
            dto.setEmail(institution.getDigitalAddress());
            apiManagerConnector.createInstitution(institutionId, dto);
            subscription = apiManagerConnector.createInstitutionSubscription(institutionId,institution.getDescription());
        }
        return subscription;
    }

    @Override
    public InstitutionApiKeys getInstitutionApiKeys(String userId) throws ResourceNotFoundException {
        log.trace("getUserSubscription start");
        log.debug("getUserSubscription userId = {}", userId);
        InstitutionApiKeys subscription = null;
        try {
            subscription = apiManagerConnector.getUserSubscription(userId);
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(String.format("No subscription found for %s userId", userId));
        }
        log.debug("getUserSubscription subscription = {}", subscription);
        log.trace("getUserSubscription end");
        return subscription;
    }

}
