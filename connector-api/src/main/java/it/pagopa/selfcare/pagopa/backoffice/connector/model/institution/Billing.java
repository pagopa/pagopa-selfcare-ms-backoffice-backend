package it.pagopa.selfcare.pagopa.backoffice.connector.model.institution;

import lombok.Data;

@Data
public class Billing {
    private String vatNumber;
    private String recipientCode;
    private Boolean publicServices;
}