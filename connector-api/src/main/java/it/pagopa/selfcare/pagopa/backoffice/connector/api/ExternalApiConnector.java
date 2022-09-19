package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;

public interface ExternalApiConnector {
    
    Institution getInstitution(String institutionId);
}
