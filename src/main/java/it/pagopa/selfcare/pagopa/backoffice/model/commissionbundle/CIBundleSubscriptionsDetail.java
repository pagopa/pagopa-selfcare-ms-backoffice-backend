package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model class that contains a list of bundle taxonomy fees details
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CIBundleSubscriptionsDetail {

    @JsonProperty("ci_bundle_fee_list")
    @Schema(description = "Creditor Institution's fees details")
    private List<CIBundleFee> ciBundleFeeList;

    @JsonProperty("bundle_request_id")
    @Schema(description = "Public bundle request id")
    private String bundleRequestId;

    @JsonProperty("bundle_offer_id")
    @Schema(description = "Public bundle offer id")
    private String bundleOfferId;

    @JsonProperty("ci_bundle_id")
    @Schema(description = "Subscription's id of a creditor institution to a public bundle")
    private String idCIBundle;
}
