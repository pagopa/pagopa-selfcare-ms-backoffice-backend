package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Model class that contains the payment service provider's info about a bundle
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PSPBundleResource extends BundleResource {

    @Schema(description = "List of taxonomies that relates to the bundle", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PSPBundleTaxonomy> bundleTaxonomies;
}
