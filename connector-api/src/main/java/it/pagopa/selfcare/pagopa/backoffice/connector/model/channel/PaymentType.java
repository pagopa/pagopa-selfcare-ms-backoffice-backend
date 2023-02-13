package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentType extends PaymentTypeBase {

    @JsonProperty("payment_type")
    private String paymentTypeCode;
}
