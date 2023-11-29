package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationResource;
import lombok.Data;

@Data
public class CreditorInstitutionStationResource extends StationResource {
    @ApiModelProperty(value = "Station's application code")
    private Long applicationCode;
    @ApiModelProperty(value = "Station's auxiliary digit")
    private Long auxDigit;
    @ApiModelProperty(value = "Station's segregation code number")
    private Long segregationCode;
    @ApiModelProperty(value = "Station's mod 4 enabled")
    private Boolean mod4;
    @ApiModelProperty(value = "Station's broadcast enabled")
    private Boolean broadcast;
}
