package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentServiceProviderDetailsResource extends  PaymentServiceProviderResource{

    @JsonProperty("abi")
    @ApiModelProperty(value = "${swagger.model.pspDetails.abi}", required = true)
    private String abi;

    @JsonProperty("bic")
    @ApiModelProperty(value = "${swagger.model.pspDetails.bic}", required = true)
    private String bic;

    @JsonProperty("transfer")
    @ApiModelProperty(value = "${swagger.model.pspDetails.transfer}", required = true)
    private Boolean transfer;

    @JsonProperty("my_bank_code")
    @ApiModelProperty(value = "${swagger.model.pspDetails.myBankCode}", required = true)
    private String myBankCode;

    @JsonProperty("stamp")
    @ApiModelProperty(value = "${swagger.model.pspDetails.stamp}", required = true)
    private Boolean stamp;

    @JsonProperty("agid_psp")
    @ApiModelProperty(value = "${swagger.model.pspDetails.agidPsp}", required = true)
    private Boolean agidPsp = false;

    @JsonProperty("tax_code")
    @ApiModelProperty(value = "${swagger.model.pspDetails.taxCode}", required = true)
    private String taxCode;

    @JsonProperty("vat_number")
    @ApiModelProperty(value = "${swagger.model.pspDetails.vatNumber}", required = true)
    private String vatNumber;
}

