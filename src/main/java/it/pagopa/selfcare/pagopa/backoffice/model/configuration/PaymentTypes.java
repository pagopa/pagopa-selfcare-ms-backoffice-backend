package it.pagopa.selfcare.pagopa.backoffice.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PaymentTypes {

    @Schema(description = "List of payment types",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("payment_types")
    private List<PaymentType> paymentTypeList;
}
