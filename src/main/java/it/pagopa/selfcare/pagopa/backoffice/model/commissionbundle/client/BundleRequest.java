package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BundleRequest {

    private String idChannel;
    private String idBrokerPsp;
    private String idCdi;
    private String abi;
    private String name;
    private String pspBusinessName;
    private String description;
    private Long paymentAmount;
    private Long minPaymentAmount;
    private Long maxPaymentAmount;
    private String paymentType;
    private Boolean digitalStamp;
    private Boolean digitalStampRestriction;
    private String touchpoint;
    private BundleType type;
    private List<String> transferCategoryList;
    private LocalDate validityDateFrom;
    private LocalDate validityDateTo;
}
