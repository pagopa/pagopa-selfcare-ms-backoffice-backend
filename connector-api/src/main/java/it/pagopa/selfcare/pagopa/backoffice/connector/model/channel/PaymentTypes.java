package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaymentTypes {

    @JsonProperty("payment_types")
    private List<PaymentType> paymentTypeList;
}
