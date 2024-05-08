package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class PaymentServiceProviderDetailsResource extends PaymentServiceProviderResource {

    @JsonProperty("abi")
    @Schema(description = "abi of the payment service provider")
    private String abi;

    @JsonProperty("bic")
    @Schema(description = "bic of the payment service provider")
    private String bic;

    @JsonProperty("my_bank_code")
    @Schema(description = "bank code of the payment service provider")
    private String myBankCode;

    @JsonProperty("stamp")
    @Schema(description = " stamp of the payment service provider")
    private Boolean stamp;

    @JsonProperty("agid_psp")
    @Schema(description = "agid code of the payment service provider")
    private Boolean agidPsp = false;

    @JsonProperty("tax_code")
    @Schema(description = "tax code of the payment service provider")
    private String taxCode;

    @JsonProperty("vat_number")
    @Schema(description = " of the payment service provider")
    private String vatNumber;
}

