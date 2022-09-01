package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionSubscription;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;

public interface ApiManagerConnector {

    void createSubscription(String userId, CreateInstitutionSubscription dto);

    UserSubscription getUserSubscription(String userId);
    
}
