package it.pagopa.selfcare.pagopa.backoffice.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PaymentTypes {

    @ApiModelProperty(value = "List of payment types", required = true)
    @JsonProperty("payment_types")
    private List<PaymentType> paymentTypeList;
}
