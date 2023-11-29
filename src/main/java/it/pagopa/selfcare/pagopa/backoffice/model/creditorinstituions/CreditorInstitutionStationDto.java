package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionStationDto {
    @ApiModelProperty(value = "Station's unique identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;

    @ApiModelProperty(value = "Station's auxiliary digit", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private Long auxDigit;

    @ApiModelProperty(value = "Station's segregation code number", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String segregationCode;

    @ApiModelProperty(value = "Station's broadcast enabled", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private Boolean broadcast;

    @ApiModelProperty(value = "Station's application code")
    private Long applicationCode;

    @ApiModelProperty(value = "Station's mod 4 enabled")
    private Boolean mod4 = true;
}
