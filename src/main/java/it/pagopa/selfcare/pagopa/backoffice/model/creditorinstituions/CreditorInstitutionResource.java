package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreditorInstitutionResource {
    @Schema(description = "Creditor Institution's code(Fiscal Code)",requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30)
    @NotBlank
    private String ciTaxCode;

    @Schema(description = "Creditor Institution activation state on ApiConfig",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean enabled;

    @Schema(description = "Creditor Institution's business name",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String businessName;

    @Schema(description = "Creditor Institution's CBill interbank code")
    private String cbillCode;

    @Schema(description = "ApplicationCode")
    private String applicationCode;

    @Schema(description = "AuxDigit maximum:3 minimum:0")
    private String auxDigit;

    @Schema(description = "SegregationCode")
    private String segregationCode;

    @Schema(description = "Mod4")
    private String mod4;

    @Schema(description = "Broadcast")
    private Boolean broadcast;

    @Nullable
    @JsonProperty("aca")
    private Boolean aca;

    @Nullable
    @JsonProperty("stand_in")
    private Boolean standIn;
}
