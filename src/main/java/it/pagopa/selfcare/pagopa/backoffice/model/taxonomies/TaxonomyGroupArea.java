package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Taxonomy Group Macro Area Instance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyGroupArea {

    private String macroAreaEcProgressive;

    private String macroAreaName;

    private String macroAreaDescription;

}