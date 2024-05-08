package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PspChannelsResource {

    @JsonProperty("channels")
    @Schema(description = " Channel list",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private List<PspChannelResource> channelsList;

}
