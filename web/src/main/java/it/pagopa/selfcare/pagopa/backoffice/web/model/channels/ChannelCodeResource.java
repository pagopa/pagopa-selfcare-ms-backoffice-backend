package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelCodeResource {

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "${swagger.model.channel.code}")
    private String channelCode;
}
