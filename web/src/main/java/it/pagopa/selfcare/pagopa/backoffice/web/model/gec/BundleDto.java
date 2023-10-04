package it.pagopa.selfcare.pagopa.backoffice.web.model.gec;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class BundleDto {

    private String idCdi;

    @JsonProperty(required = true)
    private String idChannel;

    @JsonProperty(required = true)
    private String idBrokerPsp;

    private String name;

    private String description;

    @JsonProperty(required = true)
    private String abi;

    @JsonProperty(required = true)
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
