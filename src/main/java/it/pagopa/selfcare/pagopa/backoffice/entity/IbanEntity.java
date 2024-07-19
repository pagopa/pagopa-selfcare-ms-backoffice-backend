package it.pagopa.selfcare.pagopa.backoffice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IbanEntity {

    private String ciName;
    private String ciFiscalCode;
    private String iban;
    private String status;
    private Instant validityDate;
    private String description;
    private String label;
}
