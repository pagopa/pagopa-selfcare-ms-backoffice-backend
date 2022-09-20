package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;

import java.util.List;

public interface ExternalApiConnector {
    
    Institution getInstitution(String institutionId);
    
    List<InstitutionInfo> getInstitutions(String productId);
    
}
