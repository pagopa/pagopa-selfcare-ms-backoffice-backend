package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StationCodeResource {
    @ApiModelProperty(value = "${swagger.model.station.code}", required = true)
    private String stationCode;

}
