package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Channel {
    @JsonProperty("channel_code")
    protected String channelCode;
    @JsonProperty("enabled")
    protected Boolean enabled;
    @JsonProperty("broker_description")
    protected String brokerDescription;
}
