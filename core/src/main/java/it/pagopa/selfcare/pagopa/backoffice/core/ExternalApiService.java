package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;

import java.util.Collection;

public interface ExternalApiService {
    
    InstitutionInfo getInstitution(String institutionId);
    Collection<InstitutionInfo> getInstitutions(String productId);
    
}
