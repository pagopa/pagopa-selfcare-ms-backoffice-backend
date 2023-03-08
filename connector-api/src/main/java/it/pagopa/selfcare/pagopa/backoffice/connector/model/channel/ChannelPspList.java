package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelPspList {

    @JsonProperty("payment_service_providers")
    @NotNull
    private List<ChannelPsp> psp;

    @JsonProperty("page_info")
    @NotNull
    private PageInfo pageInfo;
}
