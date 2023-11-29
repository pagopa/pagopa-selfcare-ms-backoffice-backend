package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspData {

    @ApiModelProperty(value = "PSP's Business Register number", required = true)
    @JsonProperty(value = "business_register_number", required = true)
    @NotBlank
    private String businessRegisterNumber;

    @ApiModelProperty(value = "PSP's legal register name", required = true)
    @JsonProperty(value = "legal_register_name", required = true)
    @NotBlank
    private String legalRegisterName;

    @ApiModelProperty(value = "PSP's legal register number", required = true)
    @JsonProperty(value = "legal_register_number", required = true)
    @NotBlank
    private String legalRegisterNumber;

    @ApiModelProperty(value = "PSP's ABI code", required = true)
    @JsonProperty(value = "abi_code", required = true)
    @NotBlank
    private String abiCode;

    @ApiModelProperty(value = "PSP's Vat Number group", required = true)
    @JsonProperty(value = "vat_number_group", required = true)
    @NotNull
    private Boolean vatNumberGroup;

}
