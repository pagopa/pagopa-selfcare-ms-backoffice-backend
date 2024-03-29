package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionResponse {

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
