package it.pagopa.selfcare.pagopa.backoffice.model.channels;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class ChannelDetailsResourceList {


    @JsonProperty("page_info")
    PageInfo pageInfo;
    @Schema(description = "List of psp channels", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("channels")
    private List<ChannelDetailsResource> channelDetailsResources;
}
