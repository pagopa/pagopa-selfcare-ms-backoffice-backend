package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bundle {

    @JsonProperty("idBundle")
    @Schema(description = "Document ID")
    private String id;
    private String name;
    private String description;
    @Schema(description = "Applied commission fee")
    private Long paymentAmount;
    @Schema(description = "Minimum commission fee that can be applied")
    private Long minPaymentAmount;
    @Schema(description = "Maximum commission fee that can bee applied")
    private Long maxPaymentAmount;
    @Schema(description = "Payment type for which the bundle can be applied")
    private String paymentType;
    @Schema(description = "Touch-point where the bundle can be applied")
    private String touchpoint;
    @Schema(description = "Bundle enum type")
    private BundleType type;
    @Schema(description = "List of taxonomies that relates to the bundle")
    private List<String> transferCategoryList;
    @Schema(description = "Date from which the bundle is valid")
    private LocalDate validityDateFrom;
    @Schema(description = "Date after which the bundle is expired")
    private LocalDate validityDateTo;
    @Schema(description = "Date of creation")
    private LocalDateTime insertedDate;
    @Schema(description = "Date of the last change")
    private LocalDateTime lastUpdatedDate;
    @Schema(description = "ID of the linked channel")
    private String idChannel;
    @Schema(description = "Identifier of the PSP that has created the bundle")
    private String idPsp;
    @Schema(description = "TaxCode of the linked broker PSP")
    private String idBrokerPsp;
    @Schema(description = "If the payment needs a tax stamp, mutually exclusive with digitalStampRestriction")
    private Boolean digitalStamp;
    @Schema(description = "If the payment can be payed only with a tax stamp, mutually exclusive with digitalStamp")
    private Boolean digitalStampRestriction;

    private String pspBusinessName;
    private String urlPolicyPsp;
}
