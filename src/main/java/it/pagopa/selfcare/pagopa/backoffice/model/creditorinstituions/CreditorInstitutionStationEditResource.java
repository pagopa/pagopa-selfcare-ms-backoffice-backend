package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionStationEditResource {

    @ApiModelProperty(value = "Station's unique identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty(value = "Station's auxiliary digit")
    private Long auxDigit;
    @ApiModelProperty(value = "Station's application code")
    private Long applicationCode;
    @ApiModelProperty(value = "Station's segregation code number")
    private String segregationCode;
    @ApiModelProperty(value = "Station's mod 4 enabled")
    private Boolean mod4;
    @ApiModelProperty(value = "Station's broadcast enabled")
    private Boolean broadcast;
}
