package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionStationEditResource {

    @Schema(description = "Station's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @Schema(description = "Station's auxiliary digit")
    private Long auxDigit;
    @Schema(description = "Station's application code")
    private Long applicationCode;
    @Schema(description = "Station's segregation code number")
    private String segregationCode;
    @Schema(description = "Station's mod 4 enabled")
    private Boolean mod4;
    @Schema(description = "Station's broadcast enabled")
    private Boolean broadcast;
    @JsonProperty("aca")
    private Boolean aca;
    @JsonProperty("stand_in")
    private Boolean standIn;
}
