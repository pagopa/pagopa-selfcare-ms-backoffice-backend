package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "Creditor institution's tax code")
    @JsonProperty(value = "creditor_institution_tax_code")
    private String ciTaxCode;

    @ApiModelProperty(value = "Broker's tax code")
    @JsonProperty("broker_tax_code")
    private String brokerTaxCode;

    @ApiModelProperty(value = "Station's unique identifier")
    @JsonProperty("station_code")
    private String stationCode;

    @ApiModelProperty(value = "Aux digit")
    @JsonProperty("aux_digit")
    private Long auxDigit;

    @ApiModelProperty(value = "Application code")
    @JsonProperty("application_code")
    private Long applicationCode;

    @ApiModelProperty(value = "Segregation code")
    @JsonProperty("segregation_code")
    private Long segregationCode;

    @ApiModelProperty(value = "Station's activation state")
    @JsonProperty("enabled")
    private Boolean enabled;

    @ApiModelProperty("Station's activation date")
    @JsonProperty("activation_date")
    private Instant activationDate;

    @ApiModelProperty("Station's last modified date")
    @JsonProperty("last_modified_date")
    private Instant modifiedAt;
}
