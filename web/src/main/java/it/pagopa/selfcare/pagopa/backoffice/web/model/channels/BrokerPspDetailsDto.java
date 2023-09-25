package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BrokerPspDetailsDto {

    @JsonProperty("extended_fault_bean")
    @NotNull
    private Boolean extendedFaultBean;
    @JsonProperty("broker_psp_code")

    @NotBlank
    private String brokerPspCode;

    @JsonProperty("description")
    @NotNull
    private String description;

    @JsonProperty("enabled")
    @NotNull
    private Boolean enabled;
}
