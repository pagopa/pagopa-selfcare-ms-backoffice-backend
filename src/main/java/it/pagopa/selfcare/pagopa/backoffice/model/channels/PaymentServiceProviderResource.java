package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentServiceProviderResource {

    @JsonProperty("psp_code")
    @NotBlank
    private String pspCode;

    @JsonProperty("enabled")
    @NotNull
    private Boolean enabled;

    @JsonProperty("business_name")
    @NotNull
    private String businessName;

}
