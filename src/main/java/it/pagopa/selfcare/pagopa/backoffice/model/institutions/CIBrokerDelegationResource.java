package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that holds the information for a creditor institution's broker's delegations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CIBrokerDelegationResource {

    @Schema(description = "Delegation Id")
    @JsonProperty(value = "id")
    private String id;

    @Schema(description = "Institution Id")
    @JsonProperty(value = "institution_id")
    private String institutionId;

    @Schema(description = "Institution name")
    @JsonProperty(value = "institution_name")
    private String institutionName;

    @Schema(description = "Institution tax code")
    @JsonProperty(value = "institution_tax_code")
    private String institutionTaxCode;

    @Schema(description = "Broker code")
    @JsonProperty(value = "broker_id")
    private String brokerId;

    @Schema(description = "Broker's name")
    @JsonProperty(value = "broker_name")
    private String brokerName;

    @Schema(description = "Institution type")
    @JsonProperty(value = "institution_type")
    private String institutionType;

    @Schema(description = "Creditor Institution's CBill interbank code")
    @JsonProperty(value = "cbill_code")
    private String cbillCode;

    @Schema(description = "Number of institution's stations")
    @JsonProperty(value = "institution_station_count")
    private Long institutionStationCount;

    @Schema(description = "Describe if the institution has completed the process of sign in")
    @JsonProperty(value = "is_institution_signed_in")
    private Boolean isInstitutionSignedIn;
}


