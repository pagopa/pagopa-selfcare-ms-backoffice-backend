package it.pagopa.selfcare.pagopa.backoffice.connector.model.gec;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Bundle {

    private String idCiBundle;
    private String idPsp;
    private String name;
    private String description;
    private Integer paymentAmount;
    private Integer minPaymentAmount;
    private Integer maxPaymentAmount;
    private String paymentType;
    private String touchpoint;
    private BundleType type;
    private List<String> transferCategoryList;
    private LocalDate validityDateFrom;
    private LocalDate validityDateTo;
    private LocalDateTime insertedDate;
    private LocalDateTime lastUpdatedDate;
    private String idBundle;

}
