package it.pagopa.selfcare.pagopa.backoffice.model.connector.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StationDetailsList {

    @JsonProperty("stations")
    private List<StationDetails> stationsDetailsList;

    @JsonProperty("page_info")
    @NotNull
    private PageInfo pageInfo;
}
