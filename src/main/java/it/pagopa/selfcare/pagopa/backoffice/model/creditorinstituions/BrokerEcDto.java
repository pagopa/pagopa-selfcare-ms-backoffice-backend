package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BrokerEcDto {

    @ApiModelProperty(value = "broker code", required = true)
    @Size(max = 30)
    @JsonProperty
    @NotBlank
    private String brokerCode;

    @ApiModelProperty(value = "Creditor Institution activation state on ApiConfig", required = true)
    @JsonProperty
    private Boolean enabled;

    @ApiModelProperty(value = "broker code", required = true)
    @Size(max = 30)
    @JsonProperty
    private String description;

    @ApiModelProperty(value = "xxx", required = true)
    @JsonProperty
    private Boolean extendedFaultBean;
}
