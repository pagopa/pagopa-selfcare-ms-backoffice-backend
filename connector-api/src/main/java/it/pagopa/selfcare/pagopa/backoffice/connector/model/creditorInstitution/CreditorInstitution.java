package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

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
}
