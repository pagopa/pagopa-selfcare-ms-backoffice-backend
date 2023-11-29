package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import lombok.Data;

import java.time.Instant;

@Data
public class WrapperChannelDetailsResource extends ChannelDetailsResource {

    @JsonProperty("id")
    @ApiModelProperty(value = " entities id(mongodb)")
    private String id;
    @JsonProperty("type")
    @ApiModelProperty(value = " entities type")
    private WrapperType type;
    @JsonProperty("created_at")
    @ApiModelProperty(value = " creation date")
    private Instant createdAt;
    @JsonProperty("modified_at")
    @ApiModelProperty(value = " modification date")
    private Instant modifiedAt;
    @JsonProperty("modified_by")
    @ApiModelProperty(value = " modified by")
    private String modifiedBy;
    @JsonProperty("modified_by_opt")
    @ApiModelProperty(value = " modified byoperator")
    private String modifiedByOpt;

    @ApiModelProperty(value = "channel note description by operation team")
    private String note;
}
