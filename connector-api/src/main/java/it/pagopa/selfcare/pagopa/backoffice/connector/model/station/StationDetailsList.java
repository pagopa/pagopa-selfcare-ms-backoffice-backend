package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StationDetailsList {

        @JsonProperty("stations")
        private List<StationDetails> stationsDetailsList;
}
