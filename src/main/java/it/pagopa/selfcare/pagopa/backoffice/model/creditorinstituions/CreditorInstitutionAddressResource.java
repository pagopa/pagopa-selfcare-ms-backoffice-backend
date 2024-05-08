package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreditorInstitutionAddressResource {
    @Schema(description = "Creditor Institution's physical address")
    private String location;

    @Schema(description = "Creditor Institution's city")
    private String city;

    @Schema(description = "Creditor Institution's zip code")
    private String zipCode;

    @Schema(description = "Creditor Institution's country code")
    private String countryCode;

    @Schema(description = "Creditor Institution's tax domicile")
    private String taxDomicile;
}
