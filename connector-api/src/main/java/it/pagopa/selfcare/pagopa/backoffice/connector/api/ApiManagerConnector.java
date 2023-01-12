package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserContract;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;

import java.util.List;

public interface ApiManagerConnector {

    UserContract createInstitution(String institutionId, CreateInstitutionApiKeyDto dto);

    List<InstitutionApiKeys> createInstitutionSubscription(String institutionId, String institutionName);

    void createInstitutionSubscription(String institutionId, String institutionName, String scope, String subscriptionId, String subscriptionName);

    List<InstitutionApiKeys> getInstitutionApiKeys(String userId);

    void regeneratePrimaryKey(String institutionId);

    void regenerateSecondaryKey(String institutionId);

    List<InstitutionApiKeys> getApiSubscriptions(String institutionId);

//    void deleteSubscription(String institutionId, String subscriptionId);
}
