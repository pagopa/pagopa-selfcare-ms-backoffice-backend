package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreditorInstitutionAssociatedCodeList {
    @JsonProperty("used")
    private List<CreditorInstitutionAssociatedCode> used;
    @JsonProperty("unused")
    private List<CreditorInstitutionAssociatedCode> unused;

}
