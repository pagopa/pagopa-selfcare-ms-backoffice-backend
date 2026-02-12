package it.pagopa.selfcare.pagopa.backoffice.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTypes {

    @Schema(description = "List of payment types",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("payment_types")
    private List<PaymentType> paymentTypeList;
}
