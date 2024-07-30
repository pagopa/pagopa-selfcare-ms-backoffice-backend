package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreditorInstitution {
    @JsonProperty("creditor_institution_code")
    private String creditorInstitutionCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("business_name")
    private String businessName;

    @JsonProperty("cbill_code")
    private String cbillCode;

    @JsonProperty("application_code")
    private String applicationCode;

    @JsonProperty("aux_digit")
    private String auxDigit;

    @JsonProperty("segregation_code")
    private String segregationCode;

    @JsonProperty("mod4")
    private String mod4;

    @JsonProperty("broadcast")
    private Boolean broadcast;

    @JsonProperty("stand_in")
    private Boolean standIn;
    @JsonProperty("aca")
    private Boolean aca;
}
