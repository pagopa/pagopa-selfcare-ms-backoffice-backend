package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BundleRequest {

    @Schema(description = "ID of the linked channel")
    private String idChannel;
    @Schema(description = "TaxCode of the linked broker PSP")
    private String idBrokerPsp;
    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    private String idCdi;
    @Schema(description = "Italian Banking Association numeric code used to identify banks or credit institutions")
    private String abi;
    private String name;
    @Schema(description = "Business name of the linked broker PSP")
    private String pspBusinessName;
    private String description;
    @Schema(description = "Applied commission fee")
    private Long paymentAmount;
    @Schema(description = "Minimum commission fee that can be applied")
    private Long minPaymentAmount;
    @Schema(description = "Maximum commission fee that can bee applied")
    private Long maxPaymentAmount;
    @Schema(description = "Payment type for which the bundle can be applied")
    private String paymentType;
    @Schema(description = "If the payment needs a tax stamp, mutually exclusive with digitalStampRestriction")
    private Boolean digitalStamp;
    @Schema(description = "If the payment can be payed only with a tax stamp, mutually exclusive with digitalStamp")
    private Boolean digitalStampRestriction;
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
}
