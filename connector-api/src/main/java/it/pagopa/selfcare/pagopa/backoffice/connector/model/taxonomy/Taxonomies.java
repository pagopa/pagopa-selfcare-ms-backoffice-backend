package it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class Taxonomies {

    @JsonProperty("taxonomy")
    private List<Taxonomy> taxonomies;

}
