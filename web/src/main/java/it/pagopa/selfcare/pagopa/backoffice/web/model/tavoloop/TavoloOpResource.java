package it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class TavoloOpResource {


    @ApiModelProperty(value = "${swagger.model.tavoloOp.taxCode}")
    private String taxCode;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.name}")
    private String name;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.referent}")
    private String referent;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.email}")
    private String email;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.telephone}")
    private String telephone;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.modifiedAt}")
    private Instant modifiedAt;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.modifiedBy}")
    private String modifiedBy;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.createdAt}")
    private Instant createdAt;

    @ApiModelProperty(value = "${swagger.model.tavoloOp.createdBy}")
    private String createdBy;

}
