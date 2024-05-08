package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.tavoloop.TavoloOpResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that represent the creditor institution's contacts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditorInstitutionContactsResource {

    @Schema(description = "Operative table")
    @JsonProperty(value = "operative_table")
    private TavoloOpResource operativeTable;

    @Schema(description = "Creditor institution's payment contacts")
    @JsonProperty(value = "ci_payment_contacts")
    private List<CIPaymentContact> ciPaymentContacts;
}
