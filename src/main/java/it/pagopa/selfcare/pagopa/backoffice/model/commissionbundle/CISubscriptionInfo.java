package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class that contains creditor institution's info
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CISubscriptionInfo {


    @JsonProperty("business_name")
    @Schema(description = "Creditor Institution's business name")
    private String businessName;

    @JsonProperty("creditor_institution_code")
    @Schema(description = "Creditor Institution's tax code")
    private String ciTaxCode;

    @JsonProperty("on_removal")
    @Schema(description = "Describes if the subscription has been marked for deletion")
    private Boolean onRemoval;
}
