package it.pagopa.selfcare.pagopa.backoffice.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TavoloOpDto {

    @Schema(description = "Fiscal code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String taxCode;

    @Schema(description = "Psp",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String name;

    @Schema(description = "referent",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String referent;

    @Schema(description = " contact person's email address",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String email;

    @Schema(description = "",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String telephone;

}
