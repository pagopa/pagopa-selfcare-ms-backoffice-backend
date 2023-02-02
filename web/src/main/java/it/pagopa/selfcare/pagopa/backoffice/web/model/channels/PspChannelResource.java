package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PspChannelResource {


    @JsonProperty("enabled")
    @ApiModelProperty(value = "${swagger.model.channel.enable}", required = true)
    @NotNull
    private Boolean enabled;

    @JsonProperty("channel_code")
    @ApiModelProperty(value = "${swagger.model.channel.channelCode}", required = true)
    @NotBlank
    private String channelCode;


    @JsonProperty("payment_types")
    @ApiModelProperty(value = "${swagger.model.channel.paymentTypeList}", required = true)
    private List<String> paymentTypeList;

}
