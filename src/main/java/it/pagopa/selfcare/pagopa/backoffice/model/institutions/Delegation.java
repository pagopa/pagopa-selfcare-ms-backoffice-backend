package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delegation {

    @Schema(description = "delegation Id")
    @JsonProperty(value = "id")
    private String id;

    @Schema(description = "Broker code")
    @JsonProperty(value = "broker_id")
    private String brokerId;

    @Schema(description = "broker's name")
    @JsonProperty(value = "broker_name")
    private String brokerName;

    @Schema(description = "institution Id")
    @JsonProperty(value = "institution_id")
    private String institutionId;

    @Schema(description = "institution root name")
    @JsonProperty(value = "institution_root_name")
    private String institutionRootName;

    @Schema(description = "institution name")
    @JsonProperty(value = "institution_name")
    private String institutionName;

    @Schema(description = " id product")
    @JsonProperty(value = "product_id")
    private String productId;

    @Schema(description = "delegation type")
    private String type;

    @Schema(description = "broker tax code")
    @JsonProperty(value = "broker_tax_code")
    private String brokerTaxCode;

    @Schema(description = "broker type")
    @JsonProperty(value = "broker_type")
    private String brokerType;

    @Schema(description = "institution type")
    @JsonProperty(value = "institution_type")
    private String institutionType;

    @Schema(description = "tax code")
    @JsonProperty(value = "tax_code")
    private String taxCode;
}


