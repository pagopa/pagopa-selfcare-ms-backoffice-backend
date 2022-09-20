package it.pagopa.selfcare.pagopa.backoffice.connector.model.institution;

import lombok.Data;

@Data
public class CreateInstitutionApiKeyDto {
    
    private String description;
    private String fiscalCode;
    private String email;
    
}
