package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BundleCreateResponse {
    private String idBundle;
}
