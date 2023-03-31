package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionStationEditResource {

    @ApiModelProperty(value = "${swagger.model.station.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty(value = "${swagger.station.model.auxDigit}")
    private Long auxDigit;
    @ApiModelProperty(value = "${swagger.station.model.applicationCode}")
    private Long applicationCode;
    @ApiModelProperty(value = "${swagger.station.model.segregationCode}")
    private Long segregationCode;
    @ApiModelProperty(value = "${swagger.station.model.mod4}")
    private Boolean mod4;
    @ApiModelProperty(value = "${swagger.station.model.broadcast}")
    private Boolean broadcast;
}
