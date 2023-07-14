package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IbanLabel {

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.labels.name}", required = true)
    private String name;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.labels.description}", required = true)
    private String description;
}
