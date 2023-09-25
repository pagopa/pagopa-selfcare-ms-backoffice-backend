package it.pagopa.selfcare.pagopa.backoffice.connector.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Touchpoints {

    @JsonProperty("touchpoints")
    private List<Touchpoint> touchpoints;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
