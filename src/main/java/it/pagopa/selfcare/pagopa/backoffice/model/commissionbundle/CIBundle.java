package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CIBundle extends Bundle {

    private String idPsp;
}
