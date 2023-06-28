package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PspChannel {

    @JsonProperty("enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("channel_code")
    @NotBlank
    private String channelCode;

    @JsonProperty("payment_types")
    private List<String> paymentTypeList;
}
