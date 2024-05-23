package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditorInstitutionAssociatedCode {
    private String code;

    private String name;
}
