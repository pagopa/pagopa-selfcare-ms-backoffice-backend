package it.pagopa.selfcare.pagopa.backoffice.connector.model.broker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Broker {
    @JsonProperty("broker_code")
    private String brokerCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("description")
    private String description;

    @JsonProperty("broker_details")
    private String brokerDetails;
}
