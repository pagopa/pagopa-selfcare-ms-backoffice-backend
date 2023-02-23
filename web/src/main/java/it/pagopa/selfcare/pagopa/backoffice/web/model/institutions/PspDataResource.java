package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PspDataResource {

    @ApiModelProperty(value = "${swagger.institution.model.pspData.businessRegisterNumber}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String businessRegisterNumber;

    @ApiModelProperty(value = "${swagger.institution.model.pspData.legalRegisterName}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String legalRegisterName;

    @ApiModelProperty(value = "${swagger.institution.model.pspData.legalRegisterNumber}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String legalRegisterNumber;

    @ApiModelProperty(value = "${swagger.institution.model.pspData.abiCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String abiCode;

    @ApiModelProperty(value = "${swagger.institution.model.pspData.vatNumberGroup}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean vatNumberGroup;

}
