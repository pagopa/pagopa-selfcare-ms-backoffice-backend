package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CiBundleDetails {

    private LocalDate validityDateFrom;
    private LocalDate validityDateTo;
    private String idCIBundle;
    private String ciTaxCode;
    private List<CIBundleAttribute> attributes;
}
