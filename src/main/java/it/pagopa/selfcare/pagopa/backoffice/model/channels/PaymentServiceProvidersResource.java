package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentServiceProvidersResource {

    @JsonProperty("payment_service_providers")
    @NotNull
    @Valid
    private List<PaymentServiceProviderResource> paymentServiceProviderList = null;

    @JsonProperty("page_info")
    @NotNull
    @Valid
    private PageInfo pageInfo;
}
