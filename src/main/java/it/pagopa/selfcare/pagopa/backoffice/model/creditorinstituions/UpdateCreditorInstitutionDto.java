package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateCreditorInstitutionDto {
    @Schema(description = "Creditor Institution's code(Fiscal Code)",requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30)
    @JsonProperty(required = true)
    @NotBlank
    private String creditorInstitutionCode;

    @Schema(description = "Creditor Institution activation state on ApiConfig",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private Boolean enabled;

    @Schema(description = "Creditor Institution's business name",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String businessName;

    @Schema(description = "Creditor Institution's interbank code")
    private String cbillCode;

    @Schema(description = "Creditor Institution's address object",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @JsonProperty(required = true)
    @Valid
    private CreditorInstitutionAddressDto address;

    @Schema(description = "Creditor Institution's is a psp Payment broker",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private Boolean pspPayment;

    @Schema(description = "Enables flow towards Creditor Institution in fstp mode",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private Boolean reportingFtp;

    @Schema(description = "Enables the zipping of the content that goes through fstp",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private Boolean reportingZip;
}
