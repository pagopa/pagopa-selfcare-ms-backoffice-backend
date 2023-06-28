package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentServiceProviders {

    @JsonProperty("payment_service_providers")
    private List<PaymentServiceProvider> paymentServiceProviderList = null;

    @JsonProperty("page_info")
    private PageInfo pageInfo;
}