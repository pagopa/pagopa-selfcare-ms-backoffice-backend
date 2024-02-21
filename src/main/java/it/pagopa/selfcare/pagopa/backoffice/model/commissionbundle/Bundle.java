package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
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
    private String id;
    private String name;
    private String description;
    @Schema(description = "The fees of this bundle")
    private Long paymentAmount;
    private Long minPaymentAmount;
    private Long maxPaymentAmount;
    private String paymentType;
    private String touchpoint;
    private BundleType type;
    private List<String> transferCategoryList;
    private LocalDate validityDateFrom;
    private LocalDate validityDateTo;
    private LocalDateTime insertedDate;
    private LocalDateTime lastUpdatedDate;
    private String idChannel;
    private String idBrokerPsp;
    private Boolean digitalStamp;
    private Boolean digitalStampRestriction;
}
