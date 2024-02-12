package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentServiceProviders {

    @JsonProperty("payment_service_providers")
    private List<PaymentServiceProvider> paymentServiceProviderList = null;

    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
