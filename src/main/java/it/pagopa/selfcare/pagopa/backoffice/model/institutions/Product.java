package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Schema(description = "Product's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @Schema(description = "Product's title",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String title;

    @Schema(description = "Product's description",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String description;

    @Schema(description = "URL that redirects to the public information webpage of the product")
    @JsonProperty(value = "url_public")
    private String urlPublic;

    @Schema(description = "URL that redirects to the back-office section, where is possible to manage the product")
    @JsonProperty(value = "url_bo")
    private String urlBO;
}
