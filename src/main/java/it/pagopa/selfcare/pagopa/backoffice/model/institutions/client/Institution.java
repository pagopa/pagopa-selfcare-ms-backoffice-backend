package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Institution {
    private String address;
    private String aooParentCode;
    private List<Attribute> attributes;
    private String businessRegisterPlace;
    private DataProtectionOfficer dataProtectionOfficer;
    private String description;
    private String digitalAddress;
    private String externalId;
    private List<GeographicTaxonomy> geographicTaxonomies;
    private String id;
    private Boolean imported;
    private String institutionType;
    private List<Onboarding> onboarding;
    private String origin;
    private String originId;
    private String parentDescription;
    private PaymentServiceProvider paymentServiceProvider;
    private String rea;
    private String shareCapital;
    private String subunitCode;
    private String subunitType;
    private String supportEmail;
    private String supportPhone;
    private String taxCode;
    private String zipCode;
}
