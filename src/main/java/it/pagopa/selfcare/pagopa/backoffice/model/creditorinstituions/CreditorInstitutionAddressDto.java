package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionAddressDto {
    @ApiModelProperty(value = "Creditor Institution's physical address", required = true)
    @JsonProperty(required = true)
//    @NotBlank
    private String location;

    @ApiModelProperty(value = "Creditor Institution's city", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String city;

    @ApiModelProperty(value = "Creditor Institution's zip code", required = true)
    @JsonProperty(required = true)
//    @NotBlank
    private String zipCode;

    @ApiModelProperty(value = "Creditor Institution's country code", required = true)
    @JsonProperty(required = true)
//    @NotBlank
    private String countryCode;

    @ApiModelProperty(value = "Creditor Institution's tax domicile", required = true)
    @JsonProperty(required = true)
//    @NotBlank
    private String taxDomicile;
}
