package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InstitutionApiKeys {


    @Schema(description = "Institution's subscription id",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @Schema(description = "Institution's name Api Key",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String displayName;

    @Schema(description = "Institution's primary Api Key",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String primaryKey;

    @Schema(description = "Institution's secondary Api Key",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String secondaryKey;
}
