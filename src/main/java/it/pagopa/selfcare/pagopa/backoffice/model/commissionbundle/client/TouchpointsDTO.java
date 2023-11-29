package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class TouchpointsDTO {

    @JsonProperty("touchpoints")
    private List<TouchpointDTO> touchpoints;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
