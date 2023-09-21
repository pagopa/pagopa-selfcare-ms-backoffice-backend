package it.pagopa.selfcare.pagopa.backoffice.connector.model.gec;

import lombok.Data;

import java.time.OffsetDateTime;
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
    private String type;
    private List<String> transferCategoryList;
    private OffsetDateTime validityDateFrom;
    private OffsetDateTime validityDateTo;
    private OffsetDateTime insertedDate;
    private OffsetDateTime lastUpdatedDate;
    private String idBundle;

}
