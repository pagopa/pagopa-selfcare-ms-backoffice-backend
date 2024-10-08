package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Channel {
    @JsonProperty("channel_code")
    protected String channelCode;
    @JsonProperty("enabled")
    protected Boolean enabled;
    @JsonProperty("broker_description")
    protected String brokerDescription;
    @JsonProperty("primitive_version")
    protected Integer primitiveVersion;

    @JsonIgnore
    private Instant createdAt = Instant.now(); //FIXME when these fields will be available from apiConfig
    @JsonIgnore
    private Instant modifiedAt = Instant.now(); //FIXME remove instantiation after apiConfig has modified their entities
}
