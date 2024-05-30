package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditorInstitutionAssociatedCodeList {
    @JsonProperty("used")
    private List<CreditorInstitutionAssociatedCode> used;
    @JsonProperty("unused")
    private List<CreditorInstitutionAssociatedCode> unused;

}
