package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreditorInstitutionAndBrokerDto {

    @Schema(description = "Creditor Institution",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private CreditorInstitutionDto creditorInstitutionDto;

    @Schema(description = "Broker",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private BrokerDto brokerDto;

}
