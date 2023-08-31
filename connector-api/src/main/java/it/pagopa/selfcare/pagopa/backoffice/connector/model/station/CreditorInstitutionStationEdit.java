package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreditorInstitutionStationEdit {
    @JsonProperty("station_code")
    private String stationCode;

    @JsonProperty("aux_digit")
    private Long auxDigit;

    @JsonProperty("application_code")
    private Long applicationCode;

    @JsonProperty("segregation_code")
    private String segregationCode;

    @JsonProperty("mod4")
    private Boolean mod4;

    @JsonProperty("broadcast")
    private Boolean broadcast;
}
