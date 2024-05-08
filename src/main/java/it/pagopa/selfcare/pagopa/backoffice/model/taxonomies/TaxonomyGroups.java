package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resource to be used for group recovery
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyGroups {

    @Schema(description = "List of taxonomy groups",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private List<TaxonomyGroup> taxonomyGroups;

}
