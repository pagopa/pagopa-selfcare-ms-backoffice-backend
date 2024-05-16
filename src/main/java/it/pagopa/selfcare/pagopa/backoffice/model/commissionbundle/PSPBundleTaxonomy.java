package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Model class that contains a PSP bundle's taxonomy info
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PSPBundleTaxonomy extends BundleTaxonomy {

    @Schema(description = "Creditor Institution type",requiredMode = Schema.RequiredMode.REQUIRED)
    private String ecType;

    @Schema(description = "Macro area name",requiredMode = Schema.RequiredMode.REQUIRED)
    private String macroAreaName;

    @Schema(description = "End date of validity",requiredMode = Schema.RequiredMode.REQUIRED)
    private String endDate;
}
