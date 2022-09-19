package it.pagopa.selfcare.pagopa.backoffice.connector.model.institution;

import lombok.Data;

import java.util.List;

@Data
public class Institution {
    private String id;
    private String externalId;
    private String originId;
    private String description;
    private String digitalAddress;
    private String address;
    private String zipCode;
    private String taxCode;
    private String origin;
    private InstitutionType institutionType;
    private List<Attribute> attributes;
}
