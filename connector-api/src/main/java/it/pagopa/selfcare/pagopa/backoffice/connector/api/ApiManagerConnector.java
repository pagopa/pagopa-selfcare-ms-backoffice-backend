package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;

public interface ApiManagerConnector {

    void createInstitution(String userId, CreateInstitutionApiKeyDto dto);
    
    UserSubscription createInstitutionSubscription(String institutionId, String institutionName);

    UserSubscription getUserSubscription(String userId);
    
}
