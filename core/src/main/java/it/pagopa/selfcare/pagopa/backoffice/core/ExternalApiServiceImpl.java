package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class ExternalApiServiceImpl implements ExternalApiService{

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
    public Collection<InstitutionInfo> getInstitutions(String productId) {
        log.trace("getInstitutions start");
        log.debug("getInstitutions productId = {}", productId);
        Assert.hasText(productId, A_PRODUCT_ID_IS_REQUIRED);
        List<InstitutionInfo> institutions = externalApiConnector.getInstitutions(productId);
        log.debug("getInstitutions result = {}", institutions);
        log.trace("getInstitutions end");
        return institutions;
    }



}
