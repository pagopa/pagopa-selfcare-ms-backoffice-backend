package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Model that holds the information about the association between a broker's station and a creditor institution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CIBrokerStationResource {

    @Schema(description = "Creditor institution's tax code")
    @JsonProperty(value = "creditor_institution_tax_code")
    private String ciTaxCode;

    @Schema(description = "Broker's tax code")
    @JsonProperty("broker_tax_code")
    private String brokerTaxCode;

    @Schema(description = "Station's unique identifier")
    @JsonProperty("station_code")
    private String stationCode;

    @Schema(description = "Aux digit")
    @JsonProperty("aux_digit")
    private Long auxDigit;

    @Schema(description = "Application code")
    @JsonProperty("application_code")
    private Long applicationCode;

    @Schema(description = "Segregation code")
    @JsonProperty("segregation_code")
    private Long segregationCode;

    @Schema(description = "Station's activation state")
    @JsonProperty("station_enabled")
    private Boolean stationEnabled;

    @Schema(description = "Station's activation date")
    @JsonProperty("activation_date")
    private Instant activationDate;

    @Schema(description = "Station's last modified date")
    @JsonProperty("last_modified_date")
    private Instant modifiedAt;
}
