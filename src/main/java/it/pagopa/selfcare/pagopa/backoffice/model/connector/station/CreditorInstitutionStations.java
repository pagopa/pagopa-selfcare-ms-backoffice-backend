package it.pagopa.selfcare.pagopa.backoffice.model.connector.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreditorInstitutionStations {
    @JsonProperty("stations")
    @NotNull
    private List<CreditorInstitutionStation> stationsList;
}
