package it.pagopa.selfcare.pagopa.backoffice.model.paymentsreceipts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class ReceiptModelResponse {

    private String organizationFiscalCode;
    private String iuv;
    private String debtor;
    private String paymentDateTime;
    private String status;
}