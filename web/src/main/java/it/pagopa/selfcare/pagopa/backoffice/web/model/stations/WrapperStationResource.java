package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
@Data
public class WrapperStationResource {
    @ApiModelProperty(value = "${swagger.model.station.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty(value = "${swagger.model.station.enabled}", required = true)
    @JsonProperty(required = true)
    private Boolean enabled;
    @ApiModelProperty(value = "${swagger.model.station.brokerDescription}")
    private String brokerDescription;
    @ApiModelProperty("${swagger.model.station.version}")
    @JsonProperty(required = true)
    @NotNull
    private Long version;
    @ApiModelProperty("${swagger.model.station.associatedCreditorInstitutions}")
    @JsonProperty(required = true)
    @NotNull
    private Integer associatedCreditorInstitutions;
    @ApiModelProperty("${swagger.model.station.activationDate}")
    private Instant activationDate;
    @ApiModelProperty("${swagger.model.station.createdAt}")
    private Instant createdAt;
    @ApiModelProperty("${swagger.model.station.modifiedAt}")
    private Instant modifiedAt;
    @ApiModelProperty(value = "${swagger.model.station.status}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private WrapperStatus wrapperStatus;
    @ApiModelProperty("${swagger.model.station.service}")
    private String service;
    @ApiModelProperty("${swagger.model.station.pofService}")
    private String pofService;
    @ApiModelProperty(value = "${swagger.model.station.targetHost}")
    private String targetHost;
    @ApiModelProperty(value = "${swagger.model.station.targetPort}")
    private Long targetPort;
    @ApiModelProperty(value = "${swagger.model.station.targetPath}")
    private String targetPath;
    @ApiModelProperty(value = "${swagger.model.station.targetHostPof}")
    private String targetHostPof;
    @ApiModelProperty(value = "${swagger.model.station.targetPortPof}")
    private Long targetPortPof;
    @ApiModelProperty(value = "${swagger.model.station.targetPathPof}")
    private String targetPathPof;
}
