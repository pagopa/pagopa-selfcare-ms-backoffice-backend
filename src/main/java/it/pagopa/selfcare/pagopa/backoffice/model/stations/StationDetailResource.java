package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Protocol;
import lombok.Data;


@Data
public class StationDetailResource extends StationResource {

    @ApiModelProperty("broker's details")
    private BrokerDetailsResource brokerDetails;
    @ApiModelProperty("Station's ip address")
    private String ip;
    @ApiModelProperty("Station's new password")
    private String newPassword;
    @ApiModelProperty("Station's password")
    @JsonProperty(required = true)
    private String password;
    @ApiModelProperty("Station's port")
    private Long port;
    @ApiModelProperty("Station's http protocol")
    private Protocol protocol;
    @ApiModelProperty("Station's redirect Ip")
    private String redirectIp;
    @ApiModelProperty("Station's redirect path")
    private String redirectPath;
    @ApiModelProperty("Station's redirect port")
    private Long redirectPort;
    @ApiModelProperty("Station's redirect query string")
    private String redirectQueryString;
    @ApiModelProperty("Station's redirect http protocol")
    private Protocol redirectProtocol;
    @ApiModelProperty("Station's service")
    private String service;
    @ApiModelProperty("Station's pof service")
    private String pofService;
    @ApiModelProperty("Station's broker code")
    private String brokerCode;
    @ApiModelProperty("Station's protocol v4")
    private Protocol protocol4Mod;
    @ApiModelProperty("Station's ip v4")
    private String ip4Mod;
    @ApiModelProperty("Station's v4 port")
    private Long port4Mod;
    @ApiModelProperty("Station's service 4")
    private String service4Mod;
    @ApiModelProperty(value = "Station's proxy enabled variable")
    private Boolean proxyEnabled;
    @ApiModelProperty(value = "Station's proxy host")
    private String proxyHost;
    @ApiModelProperty(value = "Station's proxy port")
    private Long proxyPort;
    @ApiModelProperty(value = "Station's proxy username")
    private String proxyUsername;
    @ApiModelProperty(value = "Station's proxy password")
    private String proxyPassword;
    @ApiModelProperty(value = "Station's max thread number")
    private Long threadNumber;
    @ApiModelProperty(value = "Station's timeoutA")
    private Long timeoutA;
    @ApiModelProperty(value = "Station's timeoutB")
    private Long timeoutB;
    @ApiModelProperty(value = "Station's timeoutC")
    private Long timeoutC;
    @ApiModelProperty(value = "Station's online flag")
    private Boolean flagOnline;
    @ApiModelProperty(value = "Station's broker object id")
    private Long brokerObjId;
    @ApiModelProperty(value = "Station's instantaneous rt dispatch")
    private Boolean rtInstantaneousDispatch;
    @ApiModelProperty(value = "Station's target host")
    private String targetHost;
    @ApiModelProperty(value = "Station target's port")
    private Long targetPort;
    @ApiModelProperty(value = "Station's target path")
    private String targetPath;
    @ApiModelProperty(value = "Station's target host POF")
    private String targetHostPof;
    @ApiModelProperty(value = "Station's target port POF")
    private Long targetPortPof;
    @ApiModelProperty(value = "Station's target path POF")
    private String targetPathPof;
    @ApiModelProperty("Station's primitive version")
    @JsonProperty(required = true)
    private Integer primitiveVersion;
    @ApiModelProperty("User that create the station")
    private String createdBy;
    @ApiModelProperty("Last user that modified the station")
    private String modifiedBy;

}
