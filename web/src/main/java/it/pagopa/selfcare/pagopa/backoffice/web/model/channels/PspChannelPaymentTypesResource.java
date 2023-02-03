package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PspChannelPaymentTypesResource {

    @ApiModelProperty(value = "${swagger.model.PspChannelPaymentTypesResource.list}", required = true)
    @JsonProperty("payment_types")
    private List<String> paymentTypeList;
}
