package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class IbanRequestDto {

    @Schema(description = "Creditor Institution's code(Fiscal Code)",requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30)
    @JsonProperty(required = true)
    @NotBlank
    private String creditorInstitutionCode;

    @Schema(description = "Filter by label")
    private String label;
}
