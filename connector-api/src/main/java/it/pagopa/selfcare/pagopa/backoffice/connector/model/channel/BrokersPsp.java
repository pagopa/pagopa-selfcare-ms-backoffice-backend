package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class BrokersPsp {

    @JsonProperty("brokers_psp")
    private List<BrokerPsp> brokerPspList;
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
