package it.pagopa.selfcare.pagopa.backoffice.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TavoloOpDto {

    @ApiModelProperty(value = "Fiscal code", required = true)
    @JsonProperty(required = true)
    private String taxCode;

    @ApiModelProperty(value = "Psp", required = true)
    @JsonProperty(required = true)
    private String name;

    @ApiModelProperty(value = "referent", required = true)
    @JsonProperty(required = true)
    private String referent;

    @ApiModelProperty(value = " contact person's email address", required = true)
    @JsonProperty(required = true)
    private String email;

    @ApiModelProperty(value = "", required = true)
    @JsonProperty(required = true)
    private String telephone;

}
