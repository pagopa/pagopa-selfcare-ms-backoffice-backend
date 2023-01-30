package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Channels {
    @JsonProperty("channels")
    private List<Channel> channelList;
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
