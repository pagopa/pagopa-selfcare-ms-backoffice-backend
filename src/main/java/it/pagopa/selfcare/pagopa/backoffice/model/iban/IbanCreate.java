package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanCreate {

    @ApiModelProperty(value = "The description the Creditor Institution gives to the iban about its usage")
    @JsonProperty("description")
    private String description;

    @ApiModelProperty(value = "The date on which the iban will expire", required = true)
    @JsonProperty("due_date")
    private OffsetDateTime dueDate;

    @ApiModelProperty(value = "the iban code", required = true)
    @JsonProperty("iban")
    private String iban;

    @ApiModelProperty(value = "True if the iban is active", required = true)
    @JsonProperty("is_active")
    private boolean active;

    @ApiModelProperty(value = "The labels array associated with the iban")
    @JsonProperty("labels")
    private List<IbanLabel> labels;

    @ApiModelProperty(value = "The date the Creditor Institution wants the iban to be used for its payments", required = true)
    @JsonProperty("validity_date")
    private OffsetDateTime validityDate;
}
