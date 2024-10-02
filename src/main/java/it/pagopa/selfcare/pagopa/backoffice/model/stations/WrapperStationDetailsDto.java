package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WrapperStationDetailsDto {
    @JsonProperty("broker_description")
    protected String brokerDescription = "";
    @Schema(description = "Station's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @Schema(description = "Station's broker code")
    private String brokerCode;
    @Schema(description = "Station's redirect Ip",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String redirectIp;
    @Schema(description = "Station's redirect path",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String redirectPath;
    @Schema(description = "Station's redirect port",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private Long redirectPort = 2L;
    @Schema(description = "Station's redirect query string",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String redirectQueryString;
    @Schema(description = "Station's redirect http protocol",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private Protocol redirectProtocol;
    @Max(2)
    @Min(1)
    @Schema(description = "Station's primitive version",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private Integer primitiveVersion;
    @Schema(description = "Station's activation state")
    private boolean enabled = true;
    @Schema(description = "Station's version")
    private Long version = 0L;
    @Schema(description = "station note description by operation team")
    private String note = "";
    @Schema(description = "Station's status")
    private WrapperStatus status = WrapperStatus.TO_CHECK;
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
    @JsonProperty(required = true)
    @Schema(description = "Url jira for StationDetail validation")
    private String validationUrl;
    @Schema(description = "Represents the authorization to use the standin mode with this station")
    private Boolean flagStandin = false;
    @Schema(description = "Flag that enables EC's payment options service")
    private Boolean isPaymentOptionsEnabled = false;
    @Schema(description = "Endpoint to all the API REST of the EC")
    private String restEndpoint;
}
