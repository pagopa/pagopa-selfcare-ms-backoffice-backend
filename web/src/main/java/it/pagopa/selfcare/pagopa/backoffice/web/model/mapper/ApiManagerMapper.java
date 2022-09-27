package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.ApiKeysResource;

public class ApiManagerMapper {

    public static ApiKeysResource toApiKeysResource(InstitutionApiKeys model) {
        ApiKeysResource resource = null;
        if (model != null) {
            resource = new ApiKeysResource();
            resource.setPrimaryKey(model.getPrimaryKey());
            resource.setSecondaryKey(model.getSecondaryKey());
        }
        return resource;
    }

}
