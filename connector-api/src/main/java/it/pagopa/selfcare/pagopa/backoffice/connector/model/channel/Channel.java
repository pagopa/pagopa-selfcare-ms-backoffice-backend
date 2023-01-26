package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Channel {
    @JsonProperty("channel_code")
    private String channelCode;
    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("broker_description")
    private String brokerDescription;
}
