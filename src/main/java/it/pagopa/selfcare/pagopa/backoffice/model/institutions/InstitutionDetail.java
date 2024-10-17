package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionDetail {

    @Schema(description = "Institution's unique internal identifier", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "id", required = true)
    @NotBlank
    private String id;

    @Schema(description = "Institution's name", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "name", required = true)
    @NotBlank
    private String description;

    @Schema(description = "Institution's unique external identifier", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "external_id", required = true)
    @NotBlank
    private String externalId;

    @Schema(description = "Institution's details origin Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "origin_id", required = true)
    @NotBlank
    private String originId;

    @Schema(description = "Institution's type")
    @JsonProperty(value = "institution_type")
    private InstitutionType institutionType;

    @Schema(description = "Institution's digitalAddress")
    @JsonProperty(value = "mail_address")
    private String digitalAddress;

    @Schema(description = "Institution onboarding status", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "status", required = true)
    private String status;

    @Schema(description = "Institution's physical address", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "address")
    private String address;

    @Schema(description = "Institution's taxCode", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "tax_code", required = true)
    @NotBlank
    private String taxCode;

    @Schema(description = "Institution data origin", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "origin", required = true)
    @NotBlank
    private String origin;

    @Schema(description = "Billing recipient code")
    @JsonProperty(value = "recipient_code")
    private String recipientCode;

    @Schema(description = "Logged user's roles on product", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "user_product_roles", required = true)
    private List<UserProductRole> userProductRoles;

    @Schema(description = "GPS, SCP, PT optional data")
    @JsonProperty(value = "company_informations")
    private CompanyInformation companyInformations;

    @Schema(description = "Institution's assistance contacts")
    @JsonProperty(value = "assistance_contacts")
    private AssistanceContact assistanceContacts;

    @Schema(description = "Payment Service Provider (PSP) specific data")
    @JsonProperty(value = "psp_data")
    @Valid
    private PspData pspData;

    @Schema(description = "Data Protection Officer (DPO) specific data")
    @JsonProperty(value = "dpo_data")
    @Valid
    private DpoData dpoData;
}
