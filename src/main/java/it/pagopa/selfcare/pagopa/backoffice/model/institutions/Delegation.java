package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delegation {

    @ApiModelProperty(value = "delegation Id")
    @JsonProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "Broker code")
    @JsonProperty(value = "broker_id")
    private String brokerId;

    @ApiModelProperty(value = "broker's name")
    @JsonProperty(value = "broker_name")
    private String brokerName;

    @ApiModelProperty(value = "institution Id")
    @JsonProperty(value = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "institution root name")
    @JsonProperty(value = "institution_root_name")
    private String institutionRootName;

    @ApiModelProperty(value = "institution name")
    @JsonProperty(value = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = " id product")
    @JsonProperty(value = "product_id")
    private String productId;

    @ApiModelProperty(value = "delegation type")
    private String type;

    @ApiModelProperty(value = "broker tax code")
    @JsonProperty(value = "broker_tax_code")
    private String brokerTaxCode;

    @ApiModelProperty(value = "broker type")
    @JsonProperty(value = "broker_type")
    private String brokerType;

    @ApiModelProperty(value = "institution type")
    @JsonProperty(value = "institution_type")
    private String institutionType;

    @ApiModelProperty(value = "tax code")
    @JsonProperty(value = "tax_code")
    private String taxCode;
}


