package it.pagopa.selfcare.pagopa.backoffice.model.connector.station;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;


@Data
public class Station {
    @JsonProperty("station_code")
    @NotBlank
    protected String stationCode;

    @JsonProperty("enabled")
    @NotNull
    protected Boolean enabled = true;

    @JsonProperty("broker_description")
    protected String brokerDescription;

    @JsonProperty("version")
    @NotNull
    protected Long version;

    @NotNull
    private Boolean isConnectionSync;

    @JsonIgnore
    private Integer associatedCreditorInstitutions = 0;
    @JsonIgnore
    private Instant activationDate;
    @JsonIgnore
    private Instant createdAt;
    @JsonIgnore
    private Instant modifiedAt;

}
