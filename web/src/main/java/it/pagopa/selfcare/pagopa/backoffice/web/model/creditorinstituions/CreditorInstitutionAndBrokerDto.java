package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreditorInstitutionAndBrokerDto {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.dto}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private CreditorInstitutionDto creditorInstitutionDto;

    @ApiModelProperty(value = "${swagger.broker.model.dto}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private BrokerDto brokerDto;

}
