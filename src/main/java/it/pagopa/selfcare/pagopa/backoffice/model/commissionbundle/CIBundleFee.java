package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class that contains the taxonomy fee specified by a creditor institution
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CIBundleFee {

    @Schema(description = "Taxonomy description")
    @JsonProperty(value = "service_type")
    private String serviceType;

    @Schema(description = "Taxonomy identifier")
    @JsonProperty(value = "specific_built_in_data")
    private String specificBuiltInData;

    @Schema(description = "Fee paid by the creditor institution's")
    @JsonProperty(value = "payment_amount")
    private Long paymentAmount;
}
