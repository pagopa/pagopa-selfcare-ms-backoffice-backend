package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreditorInstitutionDto {

    @ApiModelProperty(value = "Creditor Institution's code(Fiscal Code)", required = true)
    @Size(max = 30)
    @JsonProperty(required = true)
    @NotBlank
    private String creditorInstitutionCode;

    @ApiModelProperty(value = "Creditor Institution activation state on ApiConfig", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean enabled;

    @ApiModelProperty(value = "Creditor Institution's business name", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String businessName;
    @ApiModelProperty(value = "Creditor Institution's address object", required = true)
//    @NotNull
    @JsonProperty(required = true)
    @Valid
    private CreditorInstitutionAddressDto address;

    @ApiModelProperty(value = "Creditor Institution's is a psp Payment broker", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean pspPayment;

    @ApiModelProperty(value = "Enables flow towards Creditor Institution in fstp mode", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean reportingFtp;

    @ApiModelProperty(value = "Enables the zipping of the content that goes through fstp", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean reportingZip;
}
