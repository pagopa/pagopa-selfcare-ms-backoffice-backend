package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract;

public interface ApiManagerConnector {

    UserContract createInstitution(String institutionId, CreateInstitutionApiKeyDto dto);
    
    InstitutionApiKeys createInstitutionSubscription(String institutionId, String institutionName);

    InstitutionApiKeys getInstitutionApiKeys(String userId);
    
    void regeneratePrimaryKey(String institutionId);

    void regenerateSecondaryKey(String institutionId);
    
}
