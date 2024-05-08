package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelPspResource {

    @JsonProperty("psp_code")
    @Schema(description = "Code of the payment service provider")
    @NotBlank
    private String pspCode;

    @JsonProperty("business_name")
    @Schema(description = "business name of the payment service provider")
    @NotNull
    private String businessName;

    @JsonProperty("enabled")
    @Schema(description = "enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("tax_code")
    @Schema(description = "Payment Service Provider tax code")
    @NotNull
    private String taxCode;

    @JsonProperty("payment_types")
    @Schema(description = "List of payment types")
    @NotNull
    private List<String> paymentTypeList;
}
