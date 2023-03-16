package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;


@Data
public class Station {
    @JsonProperty("station_code")
    @NotBlank
    private String stationCode;

    @JsonProperty("enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("broker_description")
    private String brokerDescription;

    @JsonProperty("version")
    @NotNull
    protected Long version;
    protected StationStatus stationStatus = StationStatus.ACTIVE;
    protected Integer associatedCreditorInstitutions = 0;
    protected Instant activationDate = Instant.now();
    protected Instant createdAt = Instant.now(); //FIXME when these fields will be available from apiConfig
    protected Instant modifiedAt = Instant.now(); //FIXME remove instantiation after apiConfig has modified their entities

}
