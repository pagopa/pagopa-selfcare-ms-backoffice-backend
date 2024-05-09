package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bundles {

    private List<Bundle> bundles;
    private PageInfo pageInfo;
}
