package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PspChannelsResource {

    @JsonProperty("channels")
    @ApiModelProperty(value = "${swagger.model.channel.channelsList}", required = true)
    @NotNull
    private List<PspChannelResource> channelsList;

}
