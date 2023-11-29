package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class Channels {
    @JsonProperty("channels")
    private List<Channel> channelList;
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
