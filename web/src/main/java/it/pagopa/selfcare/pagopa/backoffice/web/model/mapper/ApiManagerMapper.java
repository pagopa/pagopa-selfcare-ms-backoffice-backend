package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;
import it.pagopa.selfcare.pagopa.backoffice.web.model.UserSubscriptionResource;

public class ApiManagerMapper {
    
    public static UserSubscriptionResource toSubscriptionResource(UserSubscription model){
        UserSubscriptionResource resource = null;
        if(model != null){
            resource = new UserSubscriptionResource();
            resource.setId(model.getId());
            resource.setName(model.getName());
            resource.setPrimaryKey(model.getPrimaryKey());
            resource.setSecondaryKey(model.getSecondaryKey());
        }
        return resource;
    }
}
