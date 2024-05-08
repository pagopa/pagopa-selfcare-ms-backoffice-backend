package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class StationsResource {
    @Schema(description = "List of ec stations",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private List<StationResource> stationsList;
    @Schema(description = "info pageable",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private PageInfo pageInfo;
}
