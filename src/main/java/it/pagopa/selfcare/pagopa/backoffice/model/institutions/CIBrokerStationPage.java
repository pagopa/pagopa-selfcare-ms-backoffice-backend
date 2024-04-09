package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that holds the information of the broker's stations associated with a creditor institution
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CIBrokerStationPage {

    @JsonProperty("ci_broker_stations")
    private List<CIBrokerStationResource> ciBrokerStations;

    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
