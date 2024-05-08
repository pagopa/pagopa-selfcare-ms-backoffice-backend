package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Taxonomies {

    @Schema(description = "List of taxonomy",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private List<Taxonomy> taxonomies;
}
