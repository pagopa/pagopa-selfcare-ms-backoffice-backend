package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import lombok.Data;

import java.time.Instant;

@Data
public class WrapperChannelDetailsResource extends ChannelDetailsResource {

    @JsonProperty("id")
    @ApiModelProperty(value = "${swagger.model.channel.details.id}")
    private String id;
    @JsonProperty("type")
    @ApiModelProperty(value = "${swagger.model.channel.details.type}")
    private WrapperType type;
    @JsonProperty("created_at")
    @ApiModelProperty(value = "${swagger.model.channel.details.createdAt}")
    private Instant createdAt;
    @JsonProperty("modified_at")
    @ApiModelProperty(value = "${swagger.model.channel.details.modifiedAt}")
    private Instant modifiedAt;
    @JsonProperty("modified_by")
    @ApiModelProperty(value = "${swagger.model.channel.details.modifiedBy}")
    private String modifiedBy;
    @JsonProperty("modified_by_opt")
    @ApiModelProperty(value = "${swagger.model.channel.details.modifiedByOpt}")
    private String modifiedByOpt;

    @ApiModelProperty(value = "${swagger.model.channel.details.note}")
    private String note;
}
