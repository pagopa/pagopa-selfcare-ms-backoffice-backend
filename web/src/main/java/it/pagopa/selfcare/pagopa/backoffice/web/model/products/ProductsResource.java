package it.pagopa.selfcare.pagopa.backoffice.web.model.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProductsResource {
    @ApiModelProperty(value = "${swagger.model.product.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "${swagger.model.product.title}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "${swagger.model.product.description}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;

    @ApiModelProperty("${swagger.model.product.urlPublic}")
    private String urlPublic;

    @ApiModelProperty(value = "${swagger.model.product.urlBO}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String urlBO;
}
