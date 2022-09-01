package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateSubscriptionDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;

public interface ApiManagementService {
    
    void createUserSubscription(String userId, CreateSubscriptionDto dto);
    
    UserSubscription getUserSubscription(String userId) ;
}
