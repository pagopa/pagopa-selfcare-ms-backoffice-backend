package it.pagopa.selfcare.pagopa.backoffice.model.connector.station;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.OffsetDateTime;


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

    @JsonProperty("is_connection_sync")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isConnectionSync;

    @JsonProperty("create_date")
    private OffsetDateTime createDate;

    @JsonIgnore
    private Integer associatedCreditorInstitutions = 0;
    @JsonIgnore
    private Instant activationDate;
    @JsonIgnore
    private Instant createdAt;
    @JsonIgnore
    private Instant modifiedAt;

}
