package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TransferCategoryRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CIBundleAttributeModel {

    @Schema(description = "Fee paid by the creditor institution's")
    private Long maxPaymentAmount;
    @Schema(description = "Taxonomy identifier")
    private String transferCategory;
    private TransferCategoryRelation transferCategoryRelation;
}
