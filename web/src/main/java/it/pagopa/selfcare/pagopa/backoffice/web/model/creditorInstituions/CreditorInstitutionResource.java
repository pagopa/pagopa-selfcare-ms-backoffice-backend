package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreditorInstitutionResource {
    @ApiModelProperty(value = "${swagger.creditor-institutions.model.code}", required = true)
    @Size(max = 30)
    @NotBlank
    private String creditorInstitutionCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.enabled}", required = true)
    @NotNull
    private Boolean enabled;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.businessName}", required = true)
    @NotBlank
    private String businessName;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.applicationCode}")
    private String applicationCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.auxDigit}")
    private String auxDigit;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.segregationCode}")
    private String segregationCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.mod4}")
    private String mod4;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.broadcast}")
    private Boolean broadcast;
}
