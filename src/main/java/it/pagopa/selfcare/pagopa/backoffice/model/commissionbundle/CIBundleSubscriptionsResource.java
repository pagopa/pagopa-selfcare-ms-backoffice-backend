package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model class that contains a paginated list of creditor institution's info
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CIBundleSubscriptionsResource {

    @JsonProperty("creditor_institutions_subscriptions")
    private List<CISubscriptionInfo> ciSubscriptionInfoList;

    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
