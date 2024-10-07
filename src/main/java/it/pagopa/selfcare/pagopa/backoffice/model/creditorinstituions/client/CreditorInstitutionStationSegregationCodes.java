package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Model that represent the tax code of a creditor institution and the list of its segregation codes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditorInstitutionStationSegregationCodes {

    @JsonProperty("ci_tax_code")
    @Schema(example = "02438750586", description = "The tax code of the creditor institution", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String ciTaxCode;

    @JsonProperty("segregation_code_list")
    @Schema(description = "List of segregation code used by the creditor institution in a broker station", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> segregationCodes;
}
