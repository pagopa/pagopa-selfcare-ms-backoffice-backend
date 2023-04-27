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
public class WrapperStationDetailsDto {

    @ApiModelProperty(value = "${swagger.model.station.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty("${swagger.model.station.brokerCode}")
    private String brokerCode;
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
    private Long redirectPort = 2l;
    @ApiModelProperty(value = "${swagger.model.station.redirectQueryString}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String redirectQueryString;
    @ApiModelProperty(value = "${swagger.model.station.redirectProtocol}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Protocol redirectProtocol;
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
