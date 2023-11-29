package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class WrapperChannelResource {

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "Channel code")
    @NotBlank
    protected String channelCode;

    @JsonProperty("enabled")
    protected Boolean enabled;

    @JsonProperty("broker_description")
    @ApiModelProperty(value = "Broker description. Read only field")
    protected String brokerDescription;

    @ApiModelProperty(" creation date")
    private Instant createdAt;
    @ApiModelProperty(" modification date")
    private Instant modifiedAt;
    @ApiModelProperty(value = "channel's validation status", required = true)
    @JsonProperty(required = true)
    @NotNull
    private WrapperStatus wrapperStatus;

}
