package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionApiKeys;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.ApiKeysResource;

import java.util.List;
import java.util.stream.Collectors;

public class ApiManagerMapper {

    public static ApiKeysResource toApiKeysResource(InstitutionApiKeys model) {
        ApiKeysResource resource = null;
        if (model != null) {
            resource = new ApiKeysResource();
            resource.setPrimaryKey(model.getPrimaryKey());
            resource.setSecondaryKey(model.getSecondaryKey());
            resource.setDisplayName(model.getDisplayName());
            resource.setId(model.getId());
        }
        return resource;
    }

    public static List<ApiKeysResource> toApikeysResourceList(List<InstitutionApiKeys> modelList) {
        return modelList.stream().map(ApiManagerMapper::toApiKeysResource).collect(Collectors.toList());

    }
}
