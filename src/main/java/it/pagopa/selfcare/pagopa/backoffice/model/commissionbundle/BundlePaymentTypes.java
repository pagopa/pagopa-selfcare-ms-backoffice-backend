package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundlePaymentTypes {

    @ApiModelProperty(value = "A list of payment types", required = true)
    @JsonProperty("payment_types")
    @NotNull
    List<BundlePaymentType> paymentTypes;

    @JsonProperty("page_info")
    @NotNull
    private PageInfo pageInfo;
}
