package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreditorInstitutionResource {
    @ApiModelProperty(value = "Creditor Institution's code(Fiscal Code)", required = true)
    @Size(max = 30)
    @NotBlank
    private String creditorInstitutionCode;

    @ApiModelProperty(value = "Creditor Institution activation state on ApiConfig", required = true)
    @NotNull
    private Boolean enabled;

    @ApiModelProperty(value = "Creditor Institution's business name", required = true)
    @NotBlank
    private String businessName;

    @ApiModelProperty(value = "ApplicationCode")
    private String applicationCode;

    @ApiModelProperty(value = "AuxDigit maximum:3 minimum:0")
    private String auxDigit;

    @ApiModelProperty(value = "SegregationCode")
    private String segregationCode;

    @ApiModelProperty(value = "Mod4")
    private String mod4;

    @ApiModelProperty(value = "Broadcast")
    private Boolean broadcast;
}
