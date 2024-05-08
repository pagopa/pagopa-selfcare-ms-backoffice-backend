package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class ChannelsResource {

    @Schema(description = "list of psp and ec channels",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("channels")
    private List<ChannelResource> channelList;
    @Schema(description = "info pageable",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
