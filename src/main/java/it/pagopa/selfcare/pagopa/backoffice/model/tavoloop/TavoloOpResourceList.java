package it.pagopa.selfcare.pagopa.backoffice.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TavoloOpResourceList {

    @ApiModelProperty(value = "All Tavolo operativo details", required = true)
    @JsonProperty(required = true)
    private List<TavoloOpResource> tavoloOpResourceList;
}
