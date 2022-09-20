package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;

import java.util.Collection;

public interface ExternalApiService {
    
    Institution getInstitution(String institutionId);
    
    Collection<InstitutionInfo> getInstitutions(String productId);
    
    
}
