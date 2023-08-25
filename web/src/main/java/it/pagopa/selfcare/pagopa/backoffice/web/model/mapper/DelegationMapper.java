package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.delegation.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.web.model.delegation.DelegationResource;

public class DelegationMapper {

    public static DelegationResource toResource(Delegation model) {
        DelegationResource resource = null;
        if (model != null) {
            resource = new DelegationResource();
            resource.setId(model.getId());
            resource.setType(model.getType());
            resource.setBrokerId(model.getBrokerId());
            resource.setBrokerName(model.getBrokerName());
            resource.setInstitutionId(model.getInstitutionId());
            resource.setType(model.getType());
            resource.setProductId(model.getProductId());
            resource.setInstitutionName(model.getInstitutionName());
        }
        return resource;
    }
}
