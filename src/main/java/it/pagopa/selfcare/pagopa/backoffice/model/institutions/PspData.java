package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "PSP's Business Register number", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "business_register_number", required = true)
    @NotBlank
    private String businessRegisterNumber;

    @Schema(description = "PSP's legal register name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty(value = "legal_register_name")
    private String legalRegisterName;

    @Schema(description = "PSP's legal register number", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "legal_register_number", required = true)
    @NotBlank
    private String legalRegisterNumber;

    @Schema(description = "PSP's ABI code", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "abi_code",required = true)
    @NotBlank
    private String abiCode;

    @Schema(description = "PSP's Vat Number group", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "vat_number_group", required = true)
    @NotNull
    private Boolean vatNumberGroup;

}
