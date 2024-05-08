package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class WrapperChannelResource {

    @JsonProperty("channel_code")
    @Schema(description = "Channel code")
    @NotBlank
    protected String channelCode;

    @JsonProperty("enabled")
    protected Boolean enabled;

    @JsonProperty("broker_description")
    @Schema(description = "Broker description. Read only field")
    protected String brokerDescription;

    @Schema(description = " creation date")
    private Instant createdAt;
    @Schema(description = " modification date")
    private Instant modifiedAt;
    @Schema(description = "channel's validation status",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    private WrapperStatus wrapperStatus;

}
