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
public class StationDetailsDto {

    @JsonProperty("ec_email")
    @Schema(description = " email of the ec")
    private String ecEmail;

    @Schema(description = "Station's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @Schema(description = "Station's activation state")
    private Boolean enabled;
    @Schema(description = "Station's version")
    @JsonProperty(required = true)
    @NotNull
    private Long version;
    @Schema(description = "Station broker's description")
    private String brokerDescription;
    @Schema(description = "Station's broker code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String brokerCode;
    @Schema(description = "Station's ip address")
    private String ip;
    @Schema(description = "Station's new password")
    private String newPassword;
    @Schema(description = "Station's password")
    private String password;
    @Schema(description = "Station's port")
    private Long port;
    @Schema(description = "Station's http protocol")
    private Protocol protocol = Protocol.HTTPS;
    @Schema(description = "Station's redirect Ip",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String redirectIp;
    @Schema(description = "Station's redirect path",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String redirectPath;
    @Schema(description = "Station's redirect port",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private Long redirectPort;
    @Schema(description = "Station's redirect query string",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String redirectQueryString;
    @Schema(description = "Station's redirect http protocol",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private Protocol redirectProtocol;
    @Schema(description = "Station's service")
    private String service;
    @Schema(description = "Station's pof service")
    private String pofService;
    @Schema(description = "Station's protocol v4")
    private Protocol protocol4Mod;
    @Schema(description = "Station's ip v4")
    private String ip4Mod;
    @Schema(description = "Station's v4 port")
    private Long port4Mod;
    @Schema(description = "Station's service 4")
    private String service4Mod;
    @Schema(description = "Station's proxy enabled variable")
    private Boolean proxyEnabled;
    @Schema(description = "Station's proxy host")
    private String proxyHost;
    @Schema(description = "Station's proxy port")
    private Long proxyPort;
    @Schema(description = "Station's proxy username")
    private String proxyUsername;
    @Schema(description = "Station's proxy password")
    private String proxyPassword;
    @Schema(description = "Station's max thread number")
    private Long threadNumber;
    @Schema(description = "Station's timeoutA")
    private Long timeoutA;
    @Schema(description = "Station's timeoutB")
    private Long timeoutB;
    @Schema(description = "Station's timeoutC")
    private Long timeoutC;
    @Schema(description = "Station's online flag")
    private Boolean flagOnline;
    @Schema(description = "Station's broker object id")
    private Long brokerObjId;
    @Schema(description = "Station's instantaneous rt dispatch")
    private Boolean rtInstantaneousDispatch;
    @Schema(description = "Station's target host")
    private String targetHost;
    @Schema(description = "Station target's port")
    private Long targetPort;
    @Schema(description = "Station's target path")
    private String targetPath;
    @Max(2)
    @Min(1)
    @Schema(description = "Station's primitive version",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private Integer primitiveVersion;
    @Schema(description = "station note description by operation team")
    private String note = "";
    @Schema(description = "Station's status")
    private WrapperStatus status = WrapperStatus.TO_CHECK;
    @Schema(description = "Station's target host POF")
    private String targetHostPof;
    @Schema(description = "Station's target port POF")
    private Long targetPortPof;
    @Schema(description = "Station's target path POF")
    private String targetPathPof;
    @JsonProperty(required = true)
    @Schema(description = "Url jira for StationDetail validation")
    private String validationUrl;
    @NotNull
    private Boolean flagStandin;
    @Schema(description = "Flag that enables EC's payment options service")
    private Boolean isPaymentOptionsEnabled = false;
    @Schema(description = "Endpoint to all the API REST of the EC")
    private String restEndpoint;
}
