package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class StationResource {

    @ApiModelProperty(value = "${swagger.model.station.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationId;

    @ApiModelProperty(value = "${swagger.model.station.status}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private StationStatus stationStatus;
    @ApiModelProperty("${swagger.model.station.createdAt}")
    private Instant createdAt;
    @ApiModelProperty("${swagger.model.station.activationDate}")
    private Instant activationDate;
    @ApiModelProperty("${swagger.model.station.modifiedAt}")
    private Instant modifiedAt;


}
