package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class ChannelPspListResource {

    @JsonProperty("payment_service_providers")
    @Schema(description = "enabled")
    private List<ChannelPspResource> psp;

    @JsonProperty("page_info")
    @Schema(description = "info pageable")
    private PageInfo pageInfo;
}
