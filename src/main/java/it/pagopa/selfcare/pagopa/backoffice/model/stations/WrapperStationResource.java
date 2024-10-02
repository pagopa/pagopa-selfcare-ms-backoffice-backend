package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class WrapperStationResource {
    @Schema(description = "Station's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @Schema(description = "Describe if the station is active",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private Boolean enabled;
    @Schema(description = "Station broker's description")
    private String brokerDescription;
    @Schema(description = "Station's version")
    @JsonProperty(required = true)
    @NotNull
    private Long version;
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
    @Schema(description = "Station's status",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private WrapperStatus wrapperStatus;
    @Schema(description = "Station's service")
    private String service;
    @Schema(description = "Station's pof service")
    private String pofService;
    @Schema(description = "Station's target host")
    private String targetHost;
    @Schema(description = "Station target's port")
    private Long targetPort;
    @Schema(description = "Station's target path")
    private String targetPath;
    @Schema(description = "Station's target host POF")
    private String targetHostPof;
    @Schema(description = "Station's target port POF")
    private Long targetPortPof;
    @Schema(description = "Station's target path POF")
    private String targetPathPof;
    @Schema(description = "Describe the station connection's type, true synchronous, false asynchronous")
    private Boolean isConnectionSync = true;
    @Schema(description = "Represents the authorization to use the standin mode with this station")
    private Boolean flagStandin;
    @Schema(description = "Flag that enables EC's payment options service")
    private Boolean isPaymentOptionsEnabled = false;
    @Schema(description = "Endpoint to all the API REST of the EC")
    private String restEndpoint;
}
