package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationCodeResource {
    @Schema(description = "Station's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
}
