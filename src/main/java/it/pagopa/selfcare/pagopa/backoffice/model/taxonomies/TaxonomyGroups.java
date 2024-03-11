package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "List of taxonomy groups", required = true)
    @JsonProperty(required = true)
    private List<TaxonomyGroup> taxonomyGroups;

}
