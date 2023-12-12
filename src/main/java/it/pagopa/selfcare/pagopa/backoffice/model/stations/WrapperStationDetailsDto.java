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
public class WrapperStationDetailsDto {
    @JsonProperty("broker_description")
    protected String brokerDescription = "";
    @ApiModelProperty(value = "Station's unique identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty("Station's broker code")
    private String brokerCode;
    @ApiModelProperty(value = "Station's redirect Ip", required = true)
    @JsonProperty(required = true)
    private String redirectIp;
    @ApiModelProperty(value = "Station's redirect path", required = true)
    @JsonProperty(required = true)
    private String redirectPath;
    @ApiModelProperty(value = "Station's redirect port", required = true)
    @JsonProperty(required = true)
    private Long redirectPort = 2L;
    @ApiModelProperty(value = "Station's redirect query string", required = true)
    @JsonProperty(required = true)
    private String redirectQueryString;
    @ApiModelProperty(value = "Station's redirect http protocol", required = true)
    @JsonProperty(required = true)
    private Protocol redirectProtocol;
    @Max(2)
    @Min(1)
    @ApiModelProperty(value = "Station's primitive version", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Integer primitiveVersion;
    @ApiModelProperty(value = "Station's activation state")
    private boolean enabled = true;
    @ApiModelProperty(value = "Station's version")
    private Long version = 0L;
    @ApiModelProperty(value = "station note description by operation team")
    private String note = "";
    @ApiModelProperty(value = "Station's status")
    private WrapperStatus status = WrapperStatus.TO_CHECK;
    @ApiModelProperty("Station's service")
    private String service;
    @ApiModelProperty("Station's pof service")
    private String pofService;
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
    @JsonProperty(required = true)
    @ApiModelProperty(value = "Url jira for StationDetail validation")
    private String validationUrl;
}
