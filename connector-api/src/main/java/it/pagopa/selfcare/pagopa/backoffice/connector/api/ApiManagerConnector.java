package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateSubscriptionDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;

public interface ApiManagerConnector {

    void createSubscription(String userId, CreateSubscriptionDto dto);

    UserSubscription getUserSubscription(String userId);
    
}
