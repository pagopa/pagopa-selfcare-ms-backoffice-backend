package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BrokerDetailsResource {
    @JsonProperty("extended_fault_bean")
    @NotNull
    private Boolean extendedFaultBean;

    @JsonProperty("broker_code")
    private String brokerCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("broker_details")
    private String brokerDetails;

    @JsonProperty("description")
    private String description;
}
