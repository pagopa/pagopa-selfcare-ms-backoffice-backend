package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class ChannelPspListResource {

    @JsonProperty("payment_service_providers")
    @ApiModelProperty(value = "enabled")
    private List<ChannelPspResource> psp;

    @JsonProperty("page_info")
    @ApiModelProperty(value = "info pageable")
    private PageInfo pageInfo;
}
