package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PspChannelResource {


    @JsonProperty("enabled")
    @Schema(description = "Channel enabled",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean enabled;

    @JsonProperty("channel_code")
    @Schema(description = "Channel's unique identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String channelCode;


    @JsonProperty("payment_types")
    @Schema(description = "List of payment types",requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> paymentTypeList;

}
