package it.pagopa.selfcare.pagopa.backoffice.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class TavoloOpResourceList {

    @Schema(description = "All Tavolo operativo details",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private List<TavoloOpResource> tavoloOpResourceList;
}
