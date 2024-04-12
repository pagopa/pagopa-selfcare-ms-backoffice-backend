package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerPspResource {


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
