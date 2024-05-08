package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChannelResource {

    @JsonProperty("channel_code")
    @Schema(description = "Channel code")
    @NotBlank
    protected String channelCode;

    @JsonProperty("enabled")
    protected Boolean enabled;

    @JsonProperty("broker_description")
    @Schema(description = "Broker description. Read only field")
    protected String brokerDescription;
}
