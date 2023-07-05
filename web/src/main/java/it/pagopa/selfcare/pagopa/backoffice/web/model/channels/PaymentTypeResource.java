package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentTypeResource {

    @JsonProperty("description")
    @ApiModelProperty(value = "${swagger.model.channelPaymentTypesResource.description}", required = true)
    private String description;

    @JsonProperty("payment_type")
    @ApiModelProperty(value = "${swagger.model.channelPaymentTypesResource.paymentTypeCode}", required = true)
    private String paymentTypeCode;

}
