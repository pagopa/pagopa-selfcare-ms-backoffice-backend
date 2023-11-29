package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class BundleCreate {

    @JsonProperty(value = "id_cdi")
    private String idCdi;

    @JsonProperty(value = "id_channel", required = true)
    private String idChannel;

    @JsonProperty(value = "id_broker_psp", required = true)
    private String idBrokerPsp;

    private String name;

    private String description;

    @JsonProperty(required = true)
    private String abi;

    @JsonProperty(value = "psp_business_name", required = true)
    private String pspBusinessName;

    @JsonProperty(value = "payment_amount")
    private Integer paymentAmount;

    @JsonProperty(value = "min_payment_amount")
    private Integer minPaymentAmount;

    @JsonProperty(value = "max_payment_amount")
    private Integer maxPaymentAmount;

    @JsonProperty(value = "payment_type")
    private String paymentType;

    @JsonProperty(value = "digital_stamp")
    private Boolean digitalStamp;

    @JsonProperty(value = "digital_stamp_restriction")
    private Boolean digitalStampRestriction;

    private String touchpoint;

    private BundleType type;

    @JsonProperty(value = "transfer_categories")
    private List<String> transferCategoryList;

    @JsonProperty(value = "validity_date_from")
    private LocalDate validityDateFrom;

    @JsonProperty(value = "validity_date_to")
    private LocalDate validityDateTo;

}
