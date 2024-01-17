package it.pagopa.selfcare.pagopa.backoffice.model.connector.broker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
