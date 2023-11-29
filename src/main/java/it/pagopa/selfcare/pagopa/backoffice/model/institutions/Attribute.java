package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    @ApiModelProperty(value = "Institution data origin", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String origin;

    @ApiModelProperty(value = "Institution's code", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String code;

    @ApiModelProperty(value = "Institution's name", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;
}
