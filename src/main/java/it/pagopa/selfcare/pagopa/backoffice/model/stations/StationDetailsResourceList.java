package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class StationDetailsResourceList {

    @JsonProperty("page_info")
    PageInfo pageInfo;
    @Schema(description = "Object that contains List of ec stations",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("stations")
    private List<StationDetailResource> stationsDetailsList;
}
