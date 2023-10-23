package it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TavoloOpDto {

    @ApiModelProperty(value = "${swagger.model.tavoloOp.taxCode}", required = true)
    @JsonProperty(required = true)
    private String taxCode;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.name}", required = true)
    @JsonProperty(required = true)
    private String name;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.referent}", required = true)
    @JsonProperty(required = true)
    private String referent;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.email}", required = true)
    @JsonProperty(required = true)
    private String email;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.telephone}", required = true)
    @JsonProperty(required = true)
    private String telephone;

}
