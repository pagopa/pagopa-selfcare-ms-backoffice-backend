package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Model class that contains a bundle's taxonomy info
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BundleTaxonomy {

    @Schema(description = "Taxonomy description")
    private String serviceType;

    @Schema(description = "Taxonomy identifier")
    private String specificBuiltInData;
}
