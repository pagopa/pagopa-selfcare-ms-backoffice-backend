package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;

public interface ApiManagementService {
    
    InstitutionApiKeys createInstitutionKeys(String institutionId);
    
    InstitutionApiKeys getInstitutionApiKeys(String institutionId) ;
    
    void regeneratePrimaryKey(String institutionId);
    
    void regenerateSecondaryKey(String institutionId);
}
