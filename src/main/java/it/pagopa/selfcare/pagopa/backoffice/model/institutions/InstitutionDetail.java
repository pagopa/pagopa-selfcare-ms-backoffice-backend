package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionDetail {

    @ApiModelProperty(value = "Institution's unique internal identifier", required = true)
    @JsonProperty(value = "id", required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "Institution's name", required = true)
    @JsonProperty(value = "name", required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(value = "Institution's unique external identifier", required = true)
    @JsonProperty(value = "external_id", required = true)
    @NotBlank
    private String externalId;

    @ApiModelProperty(value = "Institution's details origin Id", required = true)
    @JsonProperty(value = "origin_id", required = true)
    @NotBlank
    private String originId;

    @ApiModelProperty(value = "Institution's type")
    @JsonProperty(value = "institution_type")
    private InstitutionType institutionType;

    @ApiModelProperty(value = "Institution's digitalAddress", required = true)
    @JsonProperty(value = "mail_address")
    private String digitalAddress;

    @ApiModelProperty(value = "Institution onboarding status", required = true)
    @JsonProperty(value = "status", required = true)
    private String status;

    @ApiModelProperty(value = "Institution's physical address", required = true)
    @JsonProperty(value = "address")
    private String address;

    @ApiModelProperty(value = "Institution's taxCode", required = true)
    @JsonProperty(value = "tax_code", required = true)
    @NotBlank
    private String taxCode;

    @ApiModelProperty(value = "Institution data origin", required = true)
    @JsonProperty(value = "origin", required = true)
    @NotBlank
    private String origin;

    @ApiModelProperty(value = "Billing recipient code")
    @JsonProperty(value = "recipient_code")
    private String recipientCode;

    @ApiModelProperty(value = "Logged user's roles on product", required = true)
    @JsonProperty(value = "user_product_roles", required = true)
    private Collection<String> userProductRoles;

    @ApiModelProperty(value = "GPS, SCP, PT optional data")
    @JsonProperty(value = "company_informations")
    private CompanyInformation companyInformations;

    @ApiModelProperty(value = "Institution's assistance contacts")
    @JsonProperty(value = "assistance_contacts")
    private AssistanceContact assistanceContacts;

    @ApiModelProperty(value = "Payment Service Provider (PSP) specific data")
    @JsonProperty(value = "psp_data")
    @Valid
    private PspData pspData;

    @ApiModelProperty(value = "Data Protection Officer (DPO) specific data")
    @JsonProperty(value = "dpo_data")
    @Valid
    private DpoData dpoData;
}
