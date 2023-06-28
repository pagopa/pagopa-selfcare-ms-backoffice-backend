package it.pagopa.selfcare.pagopa.backoffice.connector.model.institution;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Billing billing;
    private Collection<String> userProductRoles;
    @JsonProperty("companyInformations")
    private BusinessData businessData;
    @JsonProperty("assistanceContacts")
    private SupportContact supportContact;
    @JsonProperty("pspData")
    private PaymentServiceProvider paymentServiceProvider;
    @JsonProperty("dpoData")
    private DataProtectionOfficer dataProtectionOfficer;

}
