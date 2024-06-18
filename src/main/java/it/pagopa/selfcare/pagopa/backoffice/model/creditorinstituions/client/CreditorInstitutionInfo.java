package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Model that represent the name and tax code of a creditor institution
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditorInstitutionInfo {

    @JsonProperty("business_name")
    @NotNull
    private String businessName;

    @JsonProperty("ci_tax_code")
    @NotBlank
    private String ciTaxCode;
}
