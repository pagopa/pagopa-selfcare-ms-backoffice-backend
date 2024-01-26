package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerInstitutionEntity {

    private String companyName;
    private String administrativeCode;
    private String taxCode;
    private Boolean intermediated;
    private String brokerCompanyName;
    private String brokerTaxCode;
    private Integer model;
    private Integer auxDigit;
    private String segregationCode;
    private String applicationCode;
    private String cbillCode;
    private String stationId;
    private String stationState;
    private Instant activationDate;
    private String version;
    private Boolean broadcast;
    private Boolean pspPayment;
}
