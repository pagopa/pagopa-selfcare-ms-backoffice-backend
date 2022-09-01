package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionSubscription;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.UserSubscription;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.CreateInstitutionSubscriptionDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions.UserSubscriptionResource;

public class ApiManagerMapper {

    public static UserSubscriptionResource toSubscriptionResource(UserSubscription model) {
        UserSubscriptionResource resource = null;
        if (model != null) {
            resource = new UserSubscriptionResource();
            resource.setId(model.getId());
            resource.setName(model.getName());
            resource.setPrimaryKey(model.getPrimaryKey());
            resource.setSecondaryKey(model.getSecondaryKey());
        }
        return resource;
    }

    public static CreateInstitutionSubscription fromDto(CreateInstitutionSubscriptionDto dto) {
        CreateInstitutionSubscription subscription = null;
        if (dto != null) {
            subscription = new CreateInstitutionSubscription();
            subscription.setDescription(dto.getDescription());
            subscription.setEmail(dto.getEmail());
            subscription.setExternalId(dto.getExternalId());
        }
        return subscription;
    }
}
