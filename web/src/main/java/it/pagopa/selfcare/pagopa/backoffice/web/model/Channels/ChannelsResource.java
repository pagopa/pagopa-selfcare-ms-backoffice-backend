package it.pagopa.selfcare.pagopa.backoffice.web.model.Channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class ChannelsResource {

    @ApiModelProperty(value = "${swagger.model.channel.list}", required = true)
    @JsonProperty("channels")
    private List<ChannelResource> channelList;
    @ApiModelProperty(value = "${swagger.model.pageinfo}", required = true)
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
