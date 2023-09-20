package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.delegation.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;

import java.util.Collection;
import java.util.List;

public interface ExternalApiService {

    Institution getInstitution(String institutionId);

    Collection<InstitutionInfo> getInstitutions(String userIdForAuth);

    List<Product> getInstitutionUserProducts(String institutionId, String userIdForAuth);

    List<Delegation> getBrokerDelegation(String institutionId, String brokerId, String productId, String mode);

}
