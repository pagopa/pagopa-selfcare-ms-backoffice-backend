package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;
import java.util.List;

@Data
public class BundlePaymentTypesResource {

    @JsonProperty("paymentTypes")
    List<BundlePaymentTypeResource> bundlePaymentTypeResources;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
