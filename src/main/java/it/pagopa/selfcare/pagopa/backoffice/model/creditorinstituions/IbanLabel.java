package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IbanLabel {

    @ApiModelProperty(value = "Label name", required = true)
    private String name;

    @ApiModelProperty(value = "Label description", required = true)
    private String description;
}
