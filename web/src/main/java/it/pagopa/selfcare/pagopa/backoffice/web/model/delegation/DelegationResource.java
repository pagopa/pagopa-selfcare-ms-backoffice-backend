package it.pagopa.selfcare.pagopa.backoffice.web.model.delegation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DelegationResource {

    @ApiModelProperty(value = "${swagger.model.delegation.brokerId}")
    private String brokerId;

    @ApiModelProperty(value = "${swagger.model.delegation.brokerName}")
    private String brokerName;

    @ApiModelProperty(value = "${swagger.model.delegation.id}")
    private String  id;

    @ApiModelProperty(value = "${swagger.model.delegation.institutionId}")
    private String  institutionId;

    @ApiModelProperty(value = "${swagger.model.delegation.institutionRootName}")
    private String  institutionRootName;

    @ApiModelProperty(value = "${swagger.model.delegation.institutionName}")
    private String institutionName;

    @ApiModelProperty(value = "${swagger.model.delegation.productId}")
    private String  productId;

    @ApiModelProperty(value = "${swagger.model.delegation.type}")
    private String type;

    @ApiModelProperty(value = "${swagger.model.delegation.brokerTaxCode}")
    private String brokerTaxCode;

    @ApiModelProperty(value = "${swagger.model.delegation.brokerType}")
    private String brokerType;

    @ApiModelProperty(value = "${swagger.model.delegation.institutionType}")
    private String institutionType;

    @ApiModelProperty(value = "${swagger.model.delegation.taxCode}")
    private String taxCode;
}
