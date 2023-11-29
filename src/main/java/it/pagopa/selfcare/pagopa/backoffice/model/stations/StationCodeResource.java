package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class StationCodeResource {
    @ApiModelProperty(value = "Station's unique identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
}
