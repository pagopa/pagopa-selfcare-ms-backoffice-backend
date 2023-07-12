package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class BrokersResource {
    @JsonProperty("brokers")
    private List<BrokerResource> brokers;
}
