package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BundleResource {

    @JsonProperty("id_ci_bundle")
    private String idCiBundle;

    @JsonProperty("id_psp")
    private String idPsp;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("payment_amount")
    private Integer paymentAmount;

    @JsonProperty("min_payment_amount")
    private Integer minPaymentAmount;

    @JsonProperty("max_payment_amount")
    private Integer maxPaymentAmount;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("touchpoint")
    private String touchpoint;

    @JsonProperty("type")
    private String type;

    @JsonProperty("transfer_category_list")
    private List<String> transferCategoryList;

    @JsonProperty("validity_date_from")
    private String validityDateFrom;

    @JsonProperty("validity_date_to")
    private String validityDateTo;

    @JsonProperty("inserted_date")
    private String insertedDate;

    @JsonProperty("last_updated_date")
    private String lastUpdatedDate;

    @JsonProperty("id_bundle")
    private String idBundle;

}
