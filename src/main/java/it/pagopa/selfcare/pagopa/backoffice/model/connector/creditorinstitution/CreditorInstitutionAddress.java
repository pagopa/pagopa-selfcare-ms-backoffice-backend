package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreditorInstitutionAddress {
    @JsonProperty("location")
    private String location;

    @JsonProperty("city")
    private String city;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("tax_domicile")
    private String taxDomicile;
}
