package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class WrapperChannelsResource {

    @ApiModelProperty(value = "list of psp and ec channels", required = true)
    @JsonProperty("channels")
    private List<WrapperChannelResource> channelList;
    @ApiModelProperty(value = "info pageable", required = true)
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
