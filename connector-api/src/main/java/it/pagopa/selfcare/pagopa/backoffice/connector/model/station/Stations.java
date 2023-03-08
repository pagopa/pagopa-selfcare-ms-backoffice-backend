package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Stations {

    @JsonProperty("stations")
    private List<Station> stationsList;
    @JsonProperty("page_info")
    private PageInfo pageInfo;

}
