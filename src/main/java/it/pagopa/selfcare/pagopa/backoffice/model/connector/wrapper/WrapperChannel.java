package it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class WrapperChannel {
    @JsonProperty(required = true)
    @NotBlank
    private String channelCode;

    @JsonProperty(required = true)
    private Boolean enabled;

    private String brokerDescription;

    private Instant createdAt;

    private Instant modifiedAt;

    @JsonProperty(required = true)
    @NotNull
    private WrapperStatus wrapperStatus;
}
