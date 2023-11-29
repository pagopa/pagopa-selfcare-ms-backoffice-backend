package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

    @Data
    public class ChannelResource {

        @JsonProperty("channel_code")
        @ApiModelProperty(value = "Channel code")
        @NotBlank
        protected String channelCode;

        @JsonProperty("enabled")
        protected Boolean enabled;

        @JsonProperty("broker_description")
        @ApiModelProperty(value = "Broker description. Read only field")
        protected String brokerDescription;
    }
