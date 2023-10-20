package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationResource;
import lombok.Data;

@Data
public class CreditorInstitutionStationResource extends StationResource {
    @ApiModelProperty(value = "${swagger.station.model.applicationCode}")
    private Long applicationCode;
    @ApiModelProperty(value = "${swagger.station.model.auxDigit}")
    private Long auxDigit;
    @ApiModelProperty(value = "${swagger.station.model.segregationCode}")
    private Long segregationCode;
    @ApiModelProperty(value = "${swagger.station.model.mod4}")
    private Boolean mod4;
    @ApiModelProperty(value = "${swagger.station.model.broadcast}")
    private Boolean broadcast;
}
