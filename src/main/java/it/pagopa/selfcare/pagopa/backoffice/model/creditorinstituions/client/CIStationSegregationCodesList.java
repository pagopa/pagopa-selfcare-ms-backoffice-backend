package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Model that represent a list of creditor institution with relative segregation codes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CIStationSegregationCodesList {

    @JsonProperty("ci_station_segregation_codes")
    @NotNull
    private List<CreditorInstitutionStationSegregationCodes> ciStationCodes;
}
