package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "A list of payment types",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("payment_types")
    @NotNull
    List<BundlePaymentType> paymentTypes;

    @JsonProperty("page_info")
    @NotNull
    private PageInfo pageInfo;
}
