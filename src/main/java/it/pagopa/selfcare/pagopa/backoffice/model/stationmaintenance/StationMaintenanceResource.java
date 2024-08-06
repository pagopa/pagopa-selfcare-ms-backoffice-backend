package it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import it.pagopa.selfcare.pagopa.backoffice.util.OffsetDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * Model class the response for station's maintenance APIs
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationMaintenanceResource {

    @JsonProperty("maintenance_id")
    @Schema(description = "Maintenance's id")
    @NotNull
    private Long maintenanceId;

    @JsonProperty("start_date_time")
    @NotNull
    @JsonFormat(pattern = Constants.ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Schema(
            example = "2024-04-01T13:00:00.000+02:00",
            description = "The start date time of the station maintenance")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime startDateTime;

    @JsonProperty("end_date_time")
    @NotNull
    @JsonFormat(pattern = Constants.ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Schema(
            example = "2024-04-01T13:00:00.000+02:00",
            description = "The end date time of the station maintenance")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime endDateTime;

    @JsonProperty("stand_in")
    @Schema(description = "StandIn flag")
    @NotNull
    private Boolean standIn;

    @JsonProperty("station_code")
    @Schema(description = "Code of the station subject of the maintenance")
    @NotNull
    private String stationCode;

    @JsonProperty("broker_code")
    @Schema(description = "Code of the broker that owns the station")
    @NotNull
    private String brokerCode;
}