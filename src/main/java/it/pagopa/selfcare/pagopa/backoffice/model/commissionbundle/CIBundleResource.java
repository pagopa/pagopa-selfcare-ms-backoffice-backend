package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Model class that contains the creditor institution's info about a bundle
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CIBundleResource extends BundleResource {

    @Schema(description = "Creditor institution bundle's status", requiredMode = Schema.RequiredMode.REQUIRED)
    private CIBundleStatus ciBundleStatus;
    @Schema(description = "Creditor institution bundle's id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ciBundleId;
    @Schema(description = "Creditor institution bundle's subscription request's id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ciRequestId;
    @Schema(description = "Creditor institution bundle's attributes that describe for each taxonomy the fee paid by the CI", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("bundleTaxonomies")
    private List<CIBundleFee> ciBundleFeeList;
}
