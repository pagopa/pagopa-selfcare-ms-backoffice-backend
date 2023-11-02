package it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class TavoloOpResource {


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

    @ApiModelProperty(value = "${swagger.model.tavoloOp.modifiedAt}", required = true)
    @JsonProperty(required = true)
    private Instant modifiedAt;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.modifiedBy}", required = true)
    @JsonProperty(required = true)
    private String modifiedBy;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.createdAt}")
    private Instant createdAt;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.createdBy}", required = true)
    @JsonProperty(required = true)
    private String createdBy;

}
