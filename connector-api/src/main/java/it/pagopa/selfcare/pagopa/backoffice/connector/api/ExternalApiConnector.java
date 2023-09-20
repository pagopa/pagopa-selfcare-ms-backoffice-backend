package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.delegation.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;

import java.util.List;

public interface ExternalApiConnector {
    
    Institution getInstitution(String institutionId);
    
    List<InstitutionInfo> getInstitutions(String userIdForAuth);
    
    List<Product> getInstitutionUserProducts(String institutionId,String userId);

    List<Delegation> getBrokerDelegation(String institutionId, String brokerId, String productId, String mode);
    
}
