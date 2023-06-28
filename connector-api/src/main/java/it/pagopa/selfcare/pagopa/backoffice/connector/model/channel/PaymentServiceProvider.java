package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentServiceProvider {

    @JsonProperty("psp_code")
    private String pspCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("business_name")
    private String businessName;

}