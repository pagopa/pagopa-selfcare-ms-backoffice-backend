package it.pagopa.selfcare.pagopa.backoffice.web.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BrokerResource {
    @JsonProperty("broker_code")
    private String brokerCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("extended_fault_bean")
    private Boolean extendedFaultBean;
}
