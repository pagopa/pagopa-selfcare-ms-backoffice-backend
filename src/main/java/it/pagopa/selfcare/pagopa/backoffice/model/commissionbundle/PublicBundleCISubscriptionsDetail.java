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
public class PublicBundleCISubscriptionsDetail {

    @JsonProperty("ci_bundle_fee_list")
    @Schema(description = "Creditor Institution's fees details")
    private List<CIBundleFee> ciBundleFeeList;
}
