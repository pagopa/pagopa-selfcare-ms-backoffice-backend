package it.pagopa.selfcare.pagopa.backoffice.model.connector.broker;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Brokers {
    @JsonProperty("brokers")
    private List<Broker> brokerList;
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
