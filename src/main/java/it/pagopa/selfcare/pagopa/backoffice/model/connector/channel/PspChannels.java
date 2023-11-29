package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PspChannels {

    @JsonProperty("channels")
    @NotNull
    private List<PspChannel> channelsList;
}
