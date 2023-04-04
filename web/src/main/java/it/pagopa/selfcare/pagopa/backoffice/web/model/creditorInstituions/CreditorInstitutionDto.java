package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreditorInstitutionDto {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.code}", required = true)
    @Size(max = 30)
    @JsonProperty(required = true)
    @NotBlank
    private String creditorInstitutionCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.enabled}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean enabled;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.businessName}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String businessName;
    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address}", required = true)
    @NotNull
    @JsonProperty(required = true)
    @Valid
    private CreditorInstitutionAddressDto address;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.pspPayment}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean pspPayment;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingFtp}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean reportingFtp;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingZip}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean reportingZip;
}
