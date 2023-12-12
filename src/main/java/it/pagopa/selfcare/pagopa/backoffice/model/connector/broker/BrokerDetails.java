package it.pagopa.selfcare.pagopa.backoffice.model.connector.broker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BrokerDetails extends Broker {
    @JsonProperty("extended_fault_bean")
    private Boolean extendedFaultBean;

}
