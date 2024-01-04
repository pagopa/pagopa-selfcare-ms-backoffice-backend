package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Iban {

    @ApiModelProperty(value = "Fiscal code of the Creditor Institution who owns the IBAN")
    @JsonProperty("ci_owner")
    private String ecOwner;

    @ApiModelProperty(value = "The Creditor Institution company name")
    @JsonProperty("company_name")
    private String companyName;

    @ApiModelProperty(value = "The description the Creditor Institution gives to the IBAN about its usage")
    @JsonProperty("description")
    private String description;

    @ApiModelProperty(value = "The date on which the IBAN will expire", required = true)
    @JsonProperty("due_date")
    private String dueDate;

    @ApiModelProperty(value = "The IBAN code", required = true)
    @JsonProperty("iban")
    private String iban;

    @ApiModelProperty(value = "True if the IBAN is active", required = true)
    @JsonProperty("is_active")
    private boolean active;

    @ApiModelProperty(value = "The labels array associated with the IBAN")
    @JsonProperty("labels")
    private List<IbanLabel> labels;

    @ApiModelProperty(value = "The date the Creditor Institution wants the IBAN to be used for its payments", required = true)
    @JsonProperty("validity_date")
    private String validityDate;

    @ApiModelProperty(value = "The date on which the IBAN has been inserted in the system")
    @JsonProperty("publication_date")
    private String publicationDate;
}
