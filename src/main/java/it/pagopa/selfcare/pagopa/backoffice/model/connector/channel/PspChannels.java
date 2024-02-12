package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PspChannels {

    @JsonProperty("channels")
    @NotNull
    private List<PspChannel> channelsList;
}
