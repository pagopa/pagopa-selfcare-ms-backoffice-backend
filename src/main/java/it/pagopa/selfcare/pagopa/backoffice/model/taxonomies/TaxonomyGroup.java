package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Instance of a Taxonomy Group
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyGroup {

    private String ecTypeCode;
    private String ecType;
    private Set<TaxonomyGroupArea> areas;

}
