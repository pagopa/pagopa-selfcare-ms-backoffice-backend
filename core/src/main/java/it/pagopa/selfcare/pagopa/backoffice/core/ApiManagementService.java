package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;

import java.util.List;

public interface ApiManagementService {

    List<InstitutionApiKeys> createInstitutionKeys(String institutionId);

    List<InstitutionApiKeys> createInstitutionKeysList(String institutionId);

    List<InstitutionApiKeys> getInstitutionApiKeys(String institutionId);

    void regeneratePrimaryKey(String institutionId);

    void regenerateSecondaryKey(String institutionId);
}
