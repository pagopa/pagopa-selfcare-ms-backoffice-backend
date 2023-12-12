package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class StationDetailsResourceList {

    @JsonProperty("page_info")
    PageInfo pageInfo;
    @ApiModelProperty(value = "Object that contains List of ec stations", required = true)
    @JsonProperty("stations")
    private List<StationDetailResource> stationsDetailsList;
}
