package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;

public interface ApiManagementService {
    
    InstitutionApiKeys createInstitutionKeys(String institutionId);
    
    InstitutionApiKeys getInstitutionApiKeys(String userId) ;
}
