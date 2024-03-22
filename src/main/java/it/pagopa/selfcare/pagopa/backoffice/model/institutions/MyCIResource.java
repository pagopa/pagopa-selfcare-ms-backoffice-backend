package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that holds the information for My creditor institutions page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCIResource {

    @ApiModelProperty(value = "Delegation Id")
    @JsonProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "Institution Id")
    @JsonProperty(value = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "Institution name")
    @JsonProperty(value = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "Institution tax code")
    @JsonProperty(value = "institution_tax_code")
    private String institutionTaxCode;

    @ApiModelProperty(value = "Broker code")
    @JsonProperty(value = "broker_id")
    private String brokerId;

    @ApiModelProperty(value = "Broker's name")
    @JsonProperty(value = "broker_name")
    private String brokerName;

    @ApiModelProperty(value = "Institution type")
    @JsonProperty(value = "institution_type")
    private String institutionType;

    @ApiModelProperty(value = "Creditor Institution's CBill interbank code")
    @JsonProperty(value = "cbill_code")
    private String cbillCode;

    @ApiModelProperty(value = "Number of institution's stations")
    @JsonProperty(value = "institution_station_count")
    private Long institutionStationCount;
}


