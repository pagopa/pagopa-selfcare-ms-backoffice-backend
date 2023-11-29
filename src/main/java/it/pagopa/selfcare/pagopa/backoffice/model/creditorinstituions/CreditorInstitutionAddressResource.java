package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreditorInstitutionAddressResource {
    @ApiModelProperty(value = "Creditor Institution's physical address", required = true)
    private String location;

    @ApiModelProperty(value = "Creditor Institution's city", required = true)
    private String city;

    @ApiModelProperty(value = "Creditor Institution's zip code", required = true)
    private String zipCode;

    @ApiModelProperty(value = "Creditor Institution's country code", required = true)
    private String countryCode;

    @ApiModelProperty(value = "Creditor Institution's tax domicile")
    private String taxDomicile;
}
