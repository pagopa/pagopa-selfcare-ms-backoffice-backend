package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class StationResource {

    @Schema(description = "Station's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @Schema(description = "Station's activation state",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private Boolean enabled;
    @Schema(description = "Station broker's description")
    private String brokerDescription;
    @Schema(description = "Station's version")
    @JsonProperty(required = true)
    @NotNull
    private Long version;
    @Schema(description = "Station's status")
    private WrapperStatus wrapperStatus;
    @Schema(description = "Number of station's creditor institutions")
    @JsonProperty(required = true)
    @NotNull
    private Integer associatedCreditorInstitutions;
    @Schema(description = "Station's activation date")
    private Instant activationDate;
    @Schema(description = "Station created on")
    private Instant createdAt;
    @Schema(description = "Station's last modified date")
    private Instant modifiedAt;

}
