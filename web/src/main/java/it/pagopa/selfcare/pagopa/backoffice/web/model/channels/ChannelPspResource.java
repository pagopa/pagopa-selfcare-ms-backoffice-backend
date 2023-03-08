package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelPspResource {

    @JsonProperty("psp_code")
    @ApiModelProperty(value = "${swagger.model.channelPspResource.pspCode}")
    @NotBlank
    private String pspCode;

    @JsonProperty("business_name")
    @ApiModelProperty(value = "${swagger.model.channelPspResource.businessName}")
    @NotNull
    private String businessName;

    @JsonProperty("enabled")
    @ApiModelProperty(value = "${swagger.model.channelPspResource.enabled}")
    @NotNull
    private Boolean enabled;

    @JsonProperty("payment_types")
    @ApiModelProperty(value = "${swagger.model.channelPspResource.paymentTypeList}")
    @NotNull
    private List<String> paymentTypeList;
}
