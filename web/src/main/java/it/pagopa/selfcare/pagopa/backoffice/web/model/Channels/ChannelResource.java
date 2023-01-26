package it.pagopa.selfcare.pagopa.backoffice.web.model.Channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChannelResource {

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "${swagger.model.channel.code}")
    @NotBlank
    private String channelCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("broker_description")
    @ApiModelProperty(value = "${swagger.model.broker.description}")
    private String brokerDescription;
}
