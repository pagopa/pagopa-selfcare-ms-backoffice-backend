package it.pagopa.selfcare.pagopa.backoffice.connector.model.institution;

import lombok.Data;

@Data
public class PaymentServiceProvider {

    private String abiCode;
    private String businessRegisterNumber;
    private String legalRegisterName;
    private String legalRegisterNumber;
    private Boolean vatNumberGroup;

}
