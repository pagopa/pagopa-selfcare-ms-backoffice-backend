package it.pagopa.selfcare.pagopa.backoffice.connector.model.delegation;

import lombok.Data;

@Data
public class Delegation {

    private String brokerId;
    private String brokerName;
    private String id;
    private String institutionId;
    private String institutionRootName;
    private String institutionName;
    private String productId;
    private String type;
    private String brokerTaxCode;
    private String brokerType;
    private String institutionType;
    private String taxCode;


}


