package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = " email of the ec")
    private String ecEmail;

    @ApiModelProperty(value = "Station's unique identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty(value = "Station's activation state")
    private Boolean enabled;
    @ApiModelProperty("Station's version")
    @JsonProperty(required = true)
    @NotNull
    private Long version;
    @ApiModelProperty(value = "Station broker's description")
    private String brokerDescription;
    @ApiModelProperty(value = "Station's broker code", required = true )
    @JsonProperty(required = true)
    @NotBlank
    private String brokerCode;
    @ApiModelProperty("Station's ip address")
    private String ip;
    @ApiModelProperty("Station's new password")
    private String newPassword;
    @ApiModelProperty("Station's password")
    private String password;
    @ApiModelProperty("Station's port")
    private Long port;
    @ApiModelProperty("Station's http protocol")
    private Protocol protocol = Protocol.HTTPS;
    @ApiModelProperty(value = "Station's redirect Ip", required = true)
    @JsonProperty(required = true)
    private String redirectIp;
    @ApiModelProperty(value = "Station's redirect path", required = true)
    @JsonProperty(required = true)
    private String redirectPath;
    @ApiModelProperty(value = "Station's redirect port", required = true)
    @JsonProperty(required = true)
    private Long redirectPort;
    @ApiModelProperty(value = "Station's redirect query string", required = true)
    @JsonProperty(required = true)
    private String redirectQueryString;
    @ApiModelProperty(value = "Station's redirect http protocol", required = true)
    @JsonProperty(required = true)
    private Protocol redirectProtocol;
    @ApiModelProperty("Station's service")
    private String service;
    @ApiModelProperty("Station's pof service")
    private String pofService;
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
    @Max(2)
    @Min(1)
    @ApiModelProperty(value = "Station's primitive version", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Integer primitiveVersion;
    @ApiModelProperty(value = "station note description by operation team")
    private String note = "";
    @ApiModelProperty(value = "Station's status")
    private WrapperStatus status = WrapperStatus.TO_CHECK;
    @ApiModelProperty(value = "Station's target host POF")
    private String targetHostPof;
    @ApiModelProperty(value = "Station's target port POF")
    private Long targetPortPof;
    @ApiModelProperty(value = "Station's target path POF")
    private String targetPathPof;
    @JsonProperty(required = true)
    @ApiModelProperty(value = "Url jira for StationDetail validation")
    private String validationUrl;

    public String getEmail(){
        String environment = System.getenv("env")!=null?System.getenv("env"):"local";
        return environment.equals("prod")? getEcEmail():System.getenv("TEST_EMAIL") ;
    }
}
