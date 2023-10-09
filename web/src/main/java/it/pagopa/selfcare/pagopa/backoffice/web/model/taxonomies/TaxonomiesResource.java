package it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaxonomiesResource {

    @ApiModelProperty(value = "${swagger.model.taxonomy.taxonomies}", required = true)
    @JsonProperty(required = true)
    private List<TaxonomyResource> taxonomies;
}
