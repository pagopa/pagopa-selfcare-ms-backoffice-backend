package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionAddressDto {
    @Schema(description = "Creditor Institution's physical address",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
//    @NotBlank
    private String location;

    @Schema(description = "Creditor Institution's city",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String city;

    @Schema(description = "Creditor Institution's zip code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
//    @NotBlank
    private String zipCode;

    @Schema(description = "Creditor Institution's country code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
//    @NotBlank
    private String countryCode;

    @Schema(description = "Creditor Institution's tax domicile",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
//    @NotBlank
    private String taxDomicile;
}
