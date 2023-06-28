package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionStationDto {
    @ApiModelProperty(value = "${swagger.model.station.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;

    @ApiModelProperty(value = "${swagger.station.model.auxDigit}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private Long auxDigit;

    @ApiModelProperty(value = "${swagger.station.model.segregationCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private Long segregationCode;

}
