package it.pagopa.selfcare.pagopa.backoffice.model.connector.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @JsonProperty("aca")
    private Boolean aca;

    @JsonProperty("stand_in")
    private Boolean standIn;
}
