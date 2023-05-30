package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
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
