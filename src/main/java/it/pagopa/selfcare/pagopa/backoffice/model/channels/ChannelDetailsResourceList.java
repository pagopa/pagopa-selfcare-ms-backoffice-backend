package it.pagopa.selfcare.pagopa.backoffice.model.channels;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class ChannelDetailsResourceList {


    @ApiModelProperty(value = "List of psp channels", required = true)
    @JsonProperty("channels")
    private List<ChannelDetailsResource> channelDetailsResources;

    @JsonProperty("page_info")
    PageInfo pageInfo;
}
