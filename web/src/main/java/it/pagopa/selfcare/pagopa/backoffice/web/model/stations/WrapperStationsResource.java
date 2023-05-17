package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;
@Data
public class WrapperStationsResource {
    @ApiModelProperty(value = "${swagger.model.stations.list}", required = true)
    @JsonProperty(required = true)
    private List<WrapperStationResource> stationsList;
    @ApiModelProperty(value = "${swagger.model.pageinfo}", required = true)
    @JsonProperty(required = true)
    private PageInfo pageInfo;
}
