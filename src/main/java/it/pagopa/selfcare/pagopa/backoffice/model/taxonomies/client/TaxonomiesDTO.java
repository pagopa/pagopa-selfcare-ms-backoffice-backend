package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaxonomiesDTO {

    @JsonProperty("taxonomy")
    private List<TaxonomyDTO> taxonomies;

}
