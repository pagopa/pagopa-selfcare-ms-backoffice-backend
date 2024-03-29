package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelPsp {

    @JsonProperty("psp_code")

    @NotBlank
    private String pspCode;

    @JsonProperty("business_name")
    @NotNull
    private String businessName;

    @JsonProperty("enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("tax_code")
    @NotNull
    private String taxCode;

    @JsonProperty("payment_types")
    @NotNull
    private List<String> paymentTypeList;
}
