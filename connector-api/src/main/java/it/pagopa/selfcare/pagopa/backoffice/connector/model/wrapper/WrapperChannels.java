package it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class WrapperChannels {
    @JsonProperty(required = true)
    private List<WrapperChannel> channelList;
    @JsonProperty(required = true)
    private PageInfo pageInfo;
}
