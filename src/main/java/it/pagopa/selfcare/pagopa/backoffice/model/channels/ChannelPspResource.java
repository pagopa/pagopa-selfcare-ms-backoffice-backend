package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelPspResource {

    @JsonProperty("psp_code")
    @ApiModelProperty(value = "Code of the payment service provider")
    @NotBlank
    private String pspCode;

    @JsonProperty("business_name")
    @ApiModelProperty(value = "business name of the payment service provider")
    @NotNull
    private String businessName;

    @JsonProperty("enabled")
    @ApiModelProperty(value = "enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("tax_code")
    @ApiModelProperty(value = "Payment Service Provider tax code")
    @NotNull
    private String taxCode;

    @JsonProperty("payment_types")
    @ApiModelProperty(value = "List of payment types")
    @NotNull
    private List<String> paymentTypeList;
}
