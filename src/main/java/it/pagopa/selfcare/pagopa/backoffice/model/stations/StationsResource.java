package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class StationsResource {
    @ApiModelProperty(value = "List of ec stations", required = true)
    @JsonProperty(required = true)
    private List<StationResource> stationsList;
    @ApiModelProperty(value = "info pageable", required = true)
    @JsonProperty(required = true)
    private PageInfo pageInfo;
}
