package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.Data;

@Data
public class PaymentServiceProvider {

    private String abiCode;
    private String businessRegisterNumber;
    private String legalRegisterName;
    private String legalRegisterNumber;
    private Boolean vatNumberGroup;

}
