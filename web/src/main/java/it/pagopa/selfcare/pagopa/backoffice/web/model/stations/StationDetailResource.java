package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Protocol;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data//FIXME fix after integration with DB
public class StationDetailResource extends StationResource {
    @ApiModelProperty("${swagger.model.station.ip}")
    private String ip;
    @ApiModelProperty("${swagger.model.station.newPassword}")
    private String newPassword;
    @ApiModelProperty("${swagger.model.station.password}")
//    @JsonProperty(required = true)
//    @NotBlank
    private String password;
    @ApiModelProperty("${swagger.model.station.port}")
    private Long port;
    @ApiModelProperty("${swagger.model.station.protocol}")
    private Protocol protocol;
    @ApiModelProperty("${swagger.model.station.redirectIp}")
    private String redirectIp;
    @ApiModelProperty("${swagger.model.station.redirectPath}")
    private String redirectPath;
    @ApiModelProperty("${swagger.model.station.redirectPort}")
    private Long redirectPort;
    @ApiModelProperty("${swagger.model.station.redirectQueryString}")
    private String redirectQueryString;
    @ApiModelProperty("${swagger.model.station.redirectProtocol}")
    private Protocol redirectProtocol;
    @ApiModelProperty("${swagger.model.station.service}")
    private String service;
    @ApiModelProperty("${swagger.model.station.pofService}")
    private String pofService;
    @ApiModelProperty("${swagger.model.station.brokerCode}")
    private String brokerCode;
    @ApiModelProperty("${swagger.model.station.protocol4Mod}")
    private Protocol protocol4Mod;
    @ApiModelProperty("${swagger.model.station.ip4Mod}")
    private String ip4Mod;
    @ApiModelProperty("${swagger.model.station.port4Mod}")
    private Long port4Mod;
    @ApiModelProperty("${swagger.model.station.service4Mod}")
    private String service4Mod;
    @ApiModelProperty(value = "${swagger.model.station.proxyEnabled}")
    private Boolean proxyEnabled;
    @ApiModelProperty(value = "${swagger.model.station.proxyHost}")
    private String proxyHost;
    @ApiModelProperty(value = "${swagger.model.station.proxyPort}")
    private Long proxyPort;
    @ApiModelProperty(value = "${swagger.model.station.proxyUsername}")
    private String proxyUsername;
    @ApiModelProperty(value = "${swagger.model.station.proxyPassword}")
    private String proxyPassword;
    @ApiModelProperty(value = "${swagger.model.station.threadNumber}")
//    @NotNull
    private Long threadNumber;
    @ApiModelProperty(value = "${swagger.model.station.timeoutA}")
    @NotNull
    private Long timeoutA;
    @ApiModelProperty(value = "${swagger.model.station.timeoutB}")
//    @NotNull
    private Long timeoutB;
    @ApiModelProperty(value = "${swagger.model.station.timeoutC}")
    @NotNull
    private Long timeoutC;
    @ApiModelProperty(value = "${swagger.model.station.flagOnline}")
    private Boolean flagOnline;
    @ApiModelProperty(value = "${swagger.model.station.brokerObjId}")
    private Long brokerObjId;
    @ApiModelProperty(value = "${swagger.model.station.rtInstantaneousDispatch}")
    private Boolean rtInstantaneousDispatch;
    @ApiModelProperty(value = "${swagger.model.station.targetHost}")
    private String targetHost;
    @ApiModelProperty(value = "${swagger.model.station.targetPort}")
    private Long targetPort;
    @ApiModelProperty(value = "${swagger.model.station.targetPath}")
    private String targetPath;
    @ApiModelProperty("${swagger.model.station.primitiveVersion}")
    @JsonProperty(required = true)
    @NotBlank
    private String primitiveVersion;
    @ApiModelProperty("${swagger.model.station.operatedBy}")
//    @JsonProperty(required = true)
//    @NotBlank
    private String operatedBy;

}
