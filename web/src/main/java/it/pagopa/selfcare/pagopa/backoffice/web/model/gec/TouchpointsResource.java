package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class TouchpointsResource {

    @JsonProperty("touchpoints")
    private List<TouchpointResource> touchpoints;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
