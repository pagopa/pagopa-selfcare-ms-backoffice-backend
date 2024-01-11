package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerIbanEntity {

    private String ciName;
    private String ciFiscalCode;
    private String iban;
    private String status;
    private Instant validityDate;
    private String description;
    private String label;
}