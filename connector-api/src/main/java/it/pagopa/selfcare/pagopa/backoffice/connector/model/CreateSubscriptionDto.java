package it.pagopa.selfcare.pagopa.backoffice.connector.model;

import lombok.Data;

@Data
public class CreateSubscriptionDto {
    private String description;
    private String externalId;
    private String email;
}
