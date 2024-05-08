package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanCreate {

    @Schema(description = "The description the Creditor Institution gives to the iban about its usage")
    @JsonProperty("description")
    private String description;

    @Schema(description = "The date on which the iban will expire",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("due_date")
    private LocalDate dueDate;

    @Schema(description = "the iban code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("iban")
    private String iban;

    @Schema(description = "True if the iban is active",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("is_active")
    private boolean active;

    @Schema(description = "The labels array associated with the iban")
    @JsonProperty("labels")
    private List<IbanLabel> labels;

    @Schema(description = "The date the Creditor Institution wants the iban to be used for its payments",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("validity_date")
    private LocalDate validityDate;
}
