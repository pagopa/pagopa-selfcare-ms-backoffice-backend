package it.pagopa.selfcare.pagopa.backoffice.connector.model.institution;

import lombok.Data;

import java.util.Collection;

@Data
public class InstitutionInfo {
    private String id;
    private String description;
    private String externalId;
    private String originId;
    private InstitutionType institutionType;
    private String digitalAddress;
    private String status;
    private String address;
    private String zipCode;
    private String taxCode;
    private String origin;
    private Collection<String> userProductRoles;

}
