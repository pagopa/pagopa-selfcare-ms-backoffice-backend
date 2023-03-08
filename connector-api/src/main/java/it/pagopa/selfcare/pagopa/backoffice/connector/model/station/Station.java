package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class Station {
    @JsonProperty("station_code")
    private String stationCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("broker_description")
    private String brokerDescription;

    @JsonProperty("version")
    private Long version;
    private StationStatus stationStatus = StationStatus.ACTIVE;
    private Integer associatedCreditorInstitutions;
    private Instant activationDate = Instant.now();
    private Instant createdAt = Instant.now(); //FIXME when these fields will be available from apiConfig
    private Instant modifiedAt = Instant.now(); //FIXME
}
