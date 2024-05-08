package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
public class InstitutionInfo {

    @NotNull
    private String id;

    private String description;

    @NotNull
    private String externalId;

    @NotNull
    private String originId;

    @NotNull
    private InstitutionType institutionType;

    private String digitalAddress;

    private String status;

    private String address;

    private String zipCode;

    private String taxCode;

    private String origin;

    private Billing billing;

    @Valid
    private List<@NotBlank @Pattern(regexp = "admin|operator", flags = Pattern.Flag.CASE_INSENSITIVE) String> userProductRoles;

    @JsonProperty("companyInformations")
    private BusinessData businessData;

    @JsonProperty("assistanceContacts")
    private SupportContact supportContact;

    @JsonProperty("pspData")
    private PaymentServiceProvider paymentServiceProvider;

    @JsonProperty("dpoData")
    private DataProtectionOfficer dataProtectionOfficer;
}
