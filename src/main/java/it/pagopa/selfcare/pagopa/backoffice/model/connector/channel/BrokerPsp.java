package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerPsp {

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
