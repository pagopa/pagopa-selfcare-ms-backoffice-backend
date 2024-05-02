package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class PaymentServiceProviderDetailsResource extends PaymentServiceProviderResource {

    @JsonProperty("abi")
    @ApiModelProperty(value = "abi of the payment service provider")
    private String abi;

    @JsonProperty("bic")
    @ApiModelProperty(value = "bic of the payment service provider")
    private String bic;

    @JsonProperty("my_bank_code")
    @ApiModelProperty(value = "bank code of the payment service provider")
    private String myBankCode;

    @JsonProperty("stamp")
    @ApiModelProperty(value = " stamp of the payment service provider")
    private Boolean stamp;

    @JsonProperty("agid_psp")
    @ApiModelProperty(value = "agid code of the payment service provider")
    private Boolean agidPsp = false;

    @JsonProperty("tax_code")
    @ApiModelProperty(value = "tax code of the payment service provider")
    private String taxCode;

    @JsonProperty("vat_number")
    @ApiModelProperty(value = " of the payment service provider")
    private String vatNumber;
}

