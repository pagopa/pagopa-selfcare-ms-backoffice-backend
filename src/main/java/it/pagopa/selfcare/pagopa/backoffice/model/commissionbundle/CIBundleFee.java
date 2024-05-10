package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Model class that contains the taxonomy fee specified by a creditor institution
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CIBundleFee extends BundleTaxonomy {

    @Schema(description = "Fee paid by the creditor institution's")
    private Long paymentAmount;
}
