package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundlePaymentType {

    @Schema(description = "The payment type unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "A user-friendly name for the payment type",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "The description related to the payment type",requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "The date on which the payment type was created",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("created_date")
    private LocalDateTime createdDate;
}
