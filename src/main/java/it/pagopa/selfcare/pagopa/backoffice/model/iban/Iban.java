package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Iban {

    @Schema(description = "Fiscal code of the Creditor Institution who owns the IBAN")
    @JsonProperty("ci_owner")
    private String ecOwner;

    @Schema(description = "The Creditor Institution company name")
    @JsonProperty("company_name")
    private String companyName;

    @Schema(description = "The description the Creditor Institution gives to the IBAN about its usage")
    @JsonProperty("description")
    private String description;

    @Schema(description = "The date on which the IBAN will expire",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("due_date")
    private String dueDate;

    @Schema(description = "The IBAN code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("iban")
    private String iban;

    @Schema(description = "True if the IBAN is active",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("is_active")
    private boolean active;

    @Schema(description = "The labels array associated with the IBAN")
    @JsonProperty("labels")
    private List<IbanLabel> labels;

    @Schema(description = "The date the Creditor Institution wants the IBAN to be used for its payments",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("validity_date")
    private String validityDate;

    @Schema(description = "The date on which the IBAN has been inserted in the system")
    @JsonProperty("publication_date")
    private String publicationDate;
}
