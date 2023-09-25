package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class StationDetailsResourceList {

        @ApiModelProperty(value = "${swagger.model.stations}", required = true)
        @JsonProperty("stations")
        private List<StationDetailResource> stationsDetailsList;

        @JsonProperty("page_info")
        PageInfo pageInfo;
}
