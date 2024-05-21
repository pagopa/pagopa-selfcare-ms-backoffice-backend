package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import lombok.Data;


@Data
public class StationDetailResource extends StationResource {

    @Schema(description = "broker's details")
    private BrokerDetailsResource brokerDetails;
    @Schema(description = "Station's ip address")
    private String ip;
    @Schema(description = "Station's new password")
    private String newPassword;
    @Schema(description = "Station's password")
    private String password;
    @Schema(description = "Station's port")
    private Long port;
    @Schema(description = "Station's http protocol")
    private Protocol protocol;
    @Schema(description = "Station's redirect Ip")
    private String redirectIp;
    @Schema(description = "Station's redirect path")
    private String redirectPath;
    @Schema(description = "Station's redirect port")
    private Long redirectPort;
    @Schema(description = "Station's redirect query string")
    private String redirectQueryString;
    @Schema(description = "Station's redirect http protocol")
    private Protocol redirectProtocol;
    @Schema(description = "Station's service")
    private String service;
    @Schema(description = "Station's pof service")
    private String pofService;
    @Schema(description = "Station's broker code")
    private String brokerCode;
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
    @Schema(description = "Station's target host POF")
    private String targetHostPof;
    @Schema(description = "Station's target port POF")
    private Long targetPortPof;
    @Schema(description = "Station's target path POF")
    private String targetPathPof;
    @Schema(description = "Station's primitive version")
    @JsonProperty(required = true)
    private Integer primitiveVersion;
    @Schema(description = "User that create the station")
    private String createdBy;
    @Schema(description = "Last user that modified the station")
    private String modifiedBy;
    @Schema(description = "Describe the station connection's type, true synchronous, false asynchronous")
    private Boolean isConnectionSync;
    @Schema(description = "Operator review note")
    private String note;

}
