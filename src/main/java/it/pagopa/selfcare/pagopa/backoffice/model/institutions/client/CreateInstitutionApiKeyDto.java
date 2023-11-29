package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.Data;

@Data
public class CreateInstitutionApiKeyDto {
    
    private String description;
    private String taxCode;
    private String email;
    
}
