package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.delegation.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    protected static final String AN_INSTITUTION_ID_IS_REQUIRED = "An institutionId is required";
    protected static final String A_PRODUCT_ID_IS_REQUIRED = "A productId is required";
    private final ExternalApiConnector externalApiConnector;


    @Autowired
    public ExternalApiServiceImpl(ExternalApiConnector externalApiConnector) {
        this.externalApiConnector = externalApiConnector;
    }

    @Override
    public Institution getInstitution(String institutionId) {
        log.trace("getInstitution start");
        log.debug("getInstitution institutionId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        Institution institution = externalApiConnector.getInstitution(institutionId);
        log.debug("getInstitution result = {}", institution);
        log.trace("getInstitution end");
        return institution;
    }

    @Override
    public Collection<InstitutionInfo> getInstitutions(String userIdForAuth) {
        log.trace("getInstitutions start");
        List<InstitutionInfo> institutions = externalApiConnector.getInstitutions(userIdForAuth);
        log.debug("getInstitutions result = {}", institutions);
        log.trace("getInstitutions end");
        return institutions;
    }

    @Override
    public List<Product> getInstitutionUserProducts(String institutionId, String userIdForAuth) {
        log.trace("getInstitutionUserProducts start");
        log.debug("getInstitutionUserProducts institutionId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        List<Product> userProducts = externalApiConnector.getInstitutionUserProducts(institutionId, userIdForAuth);
        log.debug("getInstitutionUserProducts result = {}", userProducts);
        log.trace("getInstitutionUserProducts end");
        return userProducts;
    }

    @Override
    public List<Delegation> getBrokerDelegation(String institutionId, String brokerId, String productId, String mode) {
        log.trace("getBrokerDelegation start");
        log.debug("getBrokerDelegation institutionId = {},  brokerId = {},  productId = {}", institutionId, brokerId, productId);
        List<Delegation> delegations = externalApiConnector.getBrokerDelegation(institutionId, brokerId, productId, mode);
        log.debug("getBrokerDelegation result = {}", delegations);
        log.trace("getBrokerDelegation end");
        return delegations;
    }

}
