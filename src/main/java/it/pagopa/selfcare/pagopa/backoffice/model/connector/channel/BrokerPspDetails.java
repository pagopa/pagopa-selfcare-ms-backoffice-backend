package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BrokerPspDetails extends BrokerPsp {

    @JsonProperty("extended_fault_bean")
    @NotNull
    private Boolean extendedFaultBean;
}
