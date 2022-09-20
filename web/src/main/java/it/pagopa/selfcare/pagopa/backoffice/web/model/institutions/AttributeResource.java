package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AttributeResource {
    @ApiModelProperty(value = "${swagger.model.institution.origin}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String origin;
    @ApiModelProperty(value = "${swagger.model.institution.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String code;
    @ApiModelProperty(value = "${swagger.model.institution.name}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;
}
