package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WrapperChannelDetailsResource extends ChannelDetailsResource {

    @JsonProperty("id")
    @Schema(description = " entities id(mongodb)")
    private String id;
    @JsonProperty("type")
    @Schema(description = " entities type")
    private WrapperType type;
    @JsonProperty("created_at")
    @Schema(description = " creation date")
    private Instant createdAt;
    @JsonProperty("modified_at")
    @Schema(description = " modification date")
    private Instant modifiedAt;
    @JsonProperty("modified_by")
    @Schema(description = " modified by")
    private String modifiedBy;
    @JsonProperty("modified_by_opt")
    @Schema(description = " modified byoperator")
    private String modifiedByOpt;

    @Schema(description = "channel note description by operation team")
    private String note;
}
