package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Protocol;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StationDetailsDto {

    @ApiModelProperty(value = "${swagger.model.station.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty(value = "${swagger.model.station.enabled}")
    private Boolean enabled;

    @ApiModelProperty(value = "${swagger.model.station.brokerDescription}")
    private String brokerDescription;
    @ApiModelProperty(value = "${swagger.model.station.brokerCode}", required = true )
    @JsonProperty(required = true)
    @NotBlank
    private String brokerCode;
    @ApiModelProperty("${swagger.model.station.ip}")
    private String ip;
    @ApiModelProperty("${swagger.model.station.newPassword}")
    private String newPassword;
    @ApiModelProperty("${swagger.model.station.password}")
    private String password;
    @ApiModelProperty("${swagger.model.station.port}")
    private Long port;
    @ApiModelProperty("${swagger.model.station.protocol}")
    private Protocol protocol = Protocol.HTTPS;
    @ApiModelProperty(value = "${swagger.model.station.redirectIp}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String redirectIp;
    @ApiModelProperty(value = "${swagger.model.station.redirectPath}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String redirectPath;
    @ApiModelProperty(value = "${swagger.model.station.redirectPort}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Long redirectPort;
    @ApiModelProperty(value = "${swagger.model.station.redirectQueryString}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String redirectQueryString;
    @ApiModelProperty(value = "${swagger.model.station.redirectProtocol}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Protocol redirectProtocol;
    @ApiModelProperty("${swagger.model.station.service}")
    private String service;
    @ApiModelProperty("${swagger.model.station.pofService}")
    private String pofService;
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
    private Long threadNumber;
    @ApiModelProperty(value = "${swagger.model.station.timeoutA}")
    private Long timeoutA;
    @ApiModelProperty(value = "${swagger.model.station.timeoutB}")
    private Long timeoutB;
    @ApiModelProperty(value = "${swagger.model.station.timeoutC}")
    private Long timeoutC;
    @ApiModelProperty(value = "${swagger.model.station.flagOnline}")
    private Boolean flagOnline;
    @ApiModelProperty(value = "${swagger.model.station.brokerObjId}")
    private Long brokerObjId;
    @ApiModelProperty(value = "${swagger.model.station.rtInstantaneousDispatch}")
    private Boolean rtInstantaneousDispatch;
    @ApiModelProperty(value = "${swagger.model.station.targetHost}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String targetHost;
    @ApiModelProperty(value = "${swagger.model.station.targetPort}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Long targetPort;
    @ApiModelProperty(value = "${swagger.model.station.targetPath}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private String targetPath;
    @Max(2)
    @Min(1)
    @ApiModelProperty(value = "${swagger.model.station.primitiveVersion}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Integer primitiveVersion;
    @ApiModelProperty(value = "${swagger.model.station.note}")
    private String note = "";
    @ApiModelProperty(value = "${swagger.model.station.status}")
    private WrapperStatus status = WrapperStatus.TO_CHECK;

}
