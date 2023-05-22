package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class WrapperChannelResource {

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "${swagger.model.channel.code}")
    @NotBlank
    protected String channelCode;

    @JsonProperty("enabled")
    protected Boolean enabled;

    @JsonProperty("broker_description")
    @ApiModelProperty(value = "${swagger.model.broker.description}")
    protected String brokerDescription;

    @ApiModelProperty("${swagger.model.channel.details.createdAt}")
    private Instant createdAt;
    @ApiModelProperty("${swagger.model.channel.details.modifiedAt}")
    private Instant modifiedAt;
    @ApiModelProperty(value = "${swagger.model.channel.details.status}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private WrapperStatus wrapperStatus;

}
