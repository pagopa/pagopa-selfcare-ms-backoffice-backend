package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionStationDto {
    @Schema(description = "Station's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;

    @Schema(description = "Station's auxiliary digit",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private Long auxDigit;

    @Schema(description = "Station's segregation code number",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String segregationCode;

    @Schema(description = "Station's broadcast enabled",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private Boolean broadcast;

    @Schema(description = "Station's application code")
    private Long applicationCode;

    @Schema(description = "Station's mod 4 enabled")
    private Boolean mod4 = true;

    @Nullable
    @Schema(description = "Station's ACA enabled")
    @JsonProperty("aca")
    private Boolean aca;

    @Nullable
    @Schema(description = "Station's StandIn enabled")
    @JsonProperty("stand_in")
    private Boolean standIn;
}
