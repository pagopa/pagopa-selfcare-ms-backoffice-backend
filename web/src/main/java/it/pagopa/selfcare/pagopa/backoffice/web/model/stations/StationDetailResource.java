package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
public class StationDetailResource {
    @ApiModelProperty("${swagger.model.station.id}")
    @JsonProperty(required = true)
    @NotBlank
    private String stationId;
    @ApiModelProperty("${swagger.model.station.version}")
    @JsonProperty(required = true)
    @NotBlank
    private String version;
    @ApiModelProperty("${swagger.model.station.primitiveVersion}")
    @JsonProperty(required = true)
    @NotBlank
    private String primitiveVersion;
    @ApiModelProperty("${swagger.model.station.password}")
    @JsonProperty(required = true)
    @NotBlank
    private String password;
    @ApiModelProperty("${swagger.model.station.redirectUrl}")
    @JsonProperty(required = true)
    @NotBlank
    private String redirectUrl;
    @ApiModelProperty("${swagger.model.station.activationDate}")
    @JsonProperty(required = true)
    private Instant activationDate;
    @ApiModelProperty("${swagger.model.station.targetUrl}")
    @JsonProperty(required = true)
    @NotBlank
    private String targetUrl;
    @ApiModelProperty("${swagger.model.station.targetService}")
    @JsonProperty(required = true)
    @NotBlank
    private String targetService;
    @ApiModelProperty("${swagger.model.station.targetPort}")
    @JsonProperty(required = true)
    @NotBlank
    private String targetPort;
    @ApiModelProperty("${swagger.model.station.associatedCreditorInstitutions}")
    @JsonProperty(required = true)
    @NotBlank
    private int associatedCreditorInstitutions;
    @ApiModelProperty("${swagger.model.station.modifiedAt}")
    @JsonProperty(required = true)
    private Instant modifiedAt;
    @ApiModelProperty("${swagger.model.station.operatedBy}")
    @JsonProperty(required = true)
    @NotBlank
    private String operatedBy;

}
