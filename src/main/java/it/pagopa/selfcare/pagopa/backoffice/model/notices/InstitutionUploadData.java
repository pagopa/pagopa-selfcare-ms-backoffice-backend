package it.pagopa.selfcare.pagopa.backoffice.model.notices;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstitutionUploadData {

    @Schema(description = "CI tax code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @NotEmpty
    private String taxCode;

    @Schema(description = "CI full name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @NotEmpty
    private String fullName;

    @Schema(description = "CI organization unit managing the payment ")
    private String organization;

    @Schema(description = "CI info", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @NotEmpty
    private String info;

    @Schema(description = "Boolean to refer if it has a web channel", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean webChannel;

    @Schema(description = "Boolean to refer if it has a web channel", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean appChannel;

    @Schema(description = "CI physical channel data")
    private String physicalChannel;

    @Schema(description = "CI cbill", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @NotEmpty
    private String cbill;

    @Schema(description = "Poste name")
    private String posteNane;

    @Schema(description = "Poste account number")
    private String posteAccountNumber;

    @Schema(description = "Poste auth code")
    private String posteAuth;

    @Schema(description = "Existing logo url")
    private String logo;

}
