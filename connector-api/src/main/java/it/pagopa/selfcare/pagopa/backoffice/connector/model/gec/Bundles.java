package it.pagopa.selfcare.pagopa.backoffice.connector.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Broker;
import lombok.Data;

import java.util.List;

@Data
public class Bundles {

    @JsonProperty("bundles")
    private List<Bundle> bundles;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
