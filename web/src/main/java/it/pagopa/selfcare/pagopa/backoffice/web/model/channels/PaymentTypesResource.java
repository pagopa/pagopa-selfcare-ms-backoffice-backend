package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaymentTypesResource {


    @JsonProperty("payment_types")
    @ApiModelProperty(value = "${swagger.model.channelPaymentTypesResource.list}", required = true)
    private List<PaymentTypeResource> paymentTypeList;

}
