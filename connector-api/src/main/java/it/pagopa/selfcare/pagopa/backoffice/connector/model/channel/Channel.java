package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class Channel {
    @JsonProperty("channel_code")
    protected String channelCode;
    @JsonProperty("enabled")
    protected Boolean enabled;
    @JsonProperty("broker_description")
    protected String brokerDescription;

    @JsonIgnore
    private Instant createdAt = Instant.now(); //FIXME when these fields will be available from apiConfig
    @JsonIgnore
    private Instant modifiedAt = Instant.now(); //FIXME remove instantiation after apiConfig has modified their entities
}
