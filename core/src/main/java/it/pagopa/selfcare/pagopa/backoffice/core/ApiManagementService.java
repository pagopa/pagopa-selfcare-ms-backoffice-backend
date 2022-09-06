package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;

public interface ApiManagementService {
    
    UserSubscription createInstitutionKeys(String institutionId);
    
    UserSubscription getInstitutionApiKeys(String userId) ;
}
