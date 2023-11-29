package it.pagopa.selfcare.pagopa.backoffice.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentType {

    @ApiModelProperty(value = "Description of the payment type", required = true)
    @JsonProperty("description")
    private String description;

    @ApiModelProperty(value = "Code of payment type", required = true)
    @JsonProperty("payment_type")
    private String paymentTypeCode;
}
