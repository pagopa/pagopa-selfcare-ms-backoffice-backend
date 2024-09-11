package it.pagopa.selfcare.pagopa.backoffice.model.connector.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stations {

    @JsonProperty("stations")
    private List<Station> stationsList;
    @JsonProperty("page_info")
    private PageInfo pageInfo;

}
