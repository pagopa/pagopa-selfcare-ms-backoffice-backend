package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelDetailsList {

    @JsonProperty("channels")
    private List<ChannelDetails> channelDetailsList;

    @JsonProperty("page_info")
    @NotNull
    private PageInfo pageInfo;
}
