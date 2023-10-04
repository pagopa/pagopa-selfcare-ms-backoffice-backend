package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Schema(description = "The fees of this bundle")
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
    private BundleType type;

    @JsonProperty("transfer_category_list")
    private List<String> transferCategoryList;

    @JsonProperty("validity_date_from")
    private LocalDate validityDateFrom;

    @JsonProperty("validity_date_to")
    private LocalDate validityDateTo;

    @JsonProperty("inserted_date")
    private LocalDateTime insertedDate;

    @JsonProperty("last_updated_date")
    private LocalDateTime lastUpdatedDate;

    @JsonProperty("id_bundle")
    private String idBundle;

}
