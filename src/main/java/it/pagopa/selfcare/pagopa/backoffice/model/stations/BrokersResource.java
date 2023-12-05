package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class BrokersResource {
    @JsonProperty("brokers")
    private List<BrokerResource> brokerList;

    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
