package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class BundlePaymentTypesDTO {

    @JsonProperty("paymentTypes")
    List<BundlePaymentTypeDTO> paymentTypes;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
