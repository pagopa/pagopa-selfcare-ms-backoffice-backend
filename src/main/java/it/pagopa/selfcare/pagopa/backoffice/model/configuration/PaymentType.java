package it.pagopa.selfcare.pagopa.backoffice.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PaymentType {

    @Schema(description = "Description of the payment type",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("description")
    private String description;

    @Schema(description = "Code of payment type",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("payment_type")
    private String paymentTypeCode;
}
