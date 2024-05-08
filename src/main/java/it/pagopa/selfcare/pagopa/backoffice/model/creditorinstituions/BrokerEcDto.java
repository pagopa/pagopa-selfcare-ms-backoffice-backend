package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BrokerEcDto {

    @Schema(description = "broker code",requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30)
    @JsonProperty
    @NotBlank
    private String brokerCode;

    @Schema(description = "Creditor Institution activation state on ApiConfig",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty
    private Boolean enabled;

    @Schema(description = "broker code",requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 30)
    @JsonProperty
    private String description;

    @Schema(description = "xxx",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty
    private Boolean extendedFaultBean;
}
