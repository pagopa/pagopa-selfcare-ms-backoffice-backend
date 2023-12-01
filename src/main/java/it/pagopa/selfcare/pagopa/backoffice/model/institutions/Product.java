package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "Product's unique identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "Product's title", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "Product's description", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;

    @ApiModelProperty("URL that redirects to the public information webpage of the product")
    @JsonProperty(value = "url_public")
    private String urlPublic;

    @ApiModelProperty(value = "URL that redirects to the back-office section, where is possible to manage the product")
    @JsonProperty(value = "url_bo")
    private String urlBO;
}
