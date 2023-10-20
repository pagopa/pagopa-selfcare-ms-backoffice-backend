package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
public class IbansResource {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address}", required = true)
    private List<IbanResource> ibanList;
}
