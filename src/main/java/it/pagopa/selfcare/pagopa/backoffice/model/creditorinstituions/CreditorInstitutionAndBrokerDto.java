package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreditorInstitutionAndBrokerDto {

    @ApiModelProperty(value = "Creditor Institution", required = true)
    @JsonProperty(required = true)
    @NotNull
    private CreditorInstitutionDto creditorInstitutionDto;

    @ApiModelProperty(value = "Broker", required = true)
    @JsonProperty(required = true)
    @NotNull
    private BrokerDto brokerDto;

}
