package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that holds the information for My creditor institutions page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CIBrokerDelegationPage {

    @JsonProperty("ci_broker_delegations")
    private List<CIBrokerDelegationResource> ciBrokerDelegationResources;

    @JsonProperty("page_info")
    private PageInfo pageInfo;
}


