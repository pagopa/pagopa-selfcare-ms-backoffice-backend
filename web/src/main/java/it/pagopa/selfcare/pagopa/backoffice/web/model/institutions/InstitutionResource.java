package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
public class InstitutionResource {
    
    @ApiModelProperty(value = "${swagger.model.institution.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "${swagger.model.institution.externalId}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String externalId;

    @ApiModelProperty(value = "${swagger.model.institution.originId}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String originId;

    @ApiModelProperty(value = "${swagger.model.institution.origin}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String origin;

    @ApiModelProperty(value = "${swagger.model.institution.institutionType}")
    private InstitutionType institutionType;

    @ApiModelProperty(value = "${swagger.model.institution.name}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "${swagger.model.institution.taxCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String fiscalCode;

    @ApiModelProperty(value = "${swagger.model.institution.digitalAddress}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String mailAddress;

    @ApiModelProperty(value = "${swagger.model.institution.status}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String status;

    @ApiModelProperty(value = "${swagger.model.institution.address}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String address;

    @ApiModelProperty(value = "${swagger.model.institution.productRoles}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Collection<String> userProductRoles;

    @ApiModelProperty(value = "${swagger.institution.model.recipientCode}")
    private String recipientCode;

    @ApiModelProperty(value = "${swagger.institution.model.companyInformations}")
    private CompanyInformationsResource companyInformations;

    @ApiModelProperty(value = "${swagger.institution.model.assistance}")
    private AssistanceContactsResource assistanceContacts;

    @ApiModelProperty(value = "${swagger.institution.model.pspData}")
    @Valid
    private PspDataResource pspData;

    @ApiModelProperty(value = "${swagger.institution.model.pspData.dpoData}")
    @Valid
    private DpoDataResource dpoData;
}
