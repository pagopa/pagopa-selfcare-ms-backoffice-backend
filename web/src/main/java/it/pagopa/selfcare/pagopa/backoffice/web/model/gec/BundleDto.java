package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class BundleDto {

    private String idCdi;
    private String idChannel;
    private String idBrokerPsp;
    private String name;
    private String description;
    private String abi;
    private String pspBusinessName;
    private Integer paymentAmount;
    private Integer minPaymentAmount;
    private Integer maxPaymentAmount;
    private String paymentType;
    private Boolean digitalStamp;
    private Boolean digitalStampRestriction;
    private String touchpoint;
    private BundleType type;
    private List<String> transferCategoryList;
    private LocalDate validityDateFrom;
    private LocalDate validityDateTo;

}
