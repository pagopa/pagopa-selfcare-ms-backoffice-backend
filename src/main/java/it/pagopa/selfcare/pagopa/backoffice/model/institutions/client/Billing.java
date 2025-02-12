package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.Data;

@Data
public class Billing {
    private String vatNumber;
    private String recipientCode;
    private Boolean publicServices;
}
