package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PspChannelsResource {

    @JsonProperty("channels")
    @ApiModelProperty(value = " Channel list", required = true)
    @NotNull
    private List<PspChannelResource> channelsList;

}
