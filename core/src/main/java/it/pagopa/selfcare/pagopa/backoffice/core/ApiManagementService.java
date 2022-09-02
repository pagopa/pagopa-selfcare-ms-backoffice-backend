package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionSubscription;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;

public interface ApiManagementService {
    
    void createUserSubscription(String userId, CreateInstitutionSubscription dto);
    
    UserSubscription getUserSubscription(String userId) ;
}
