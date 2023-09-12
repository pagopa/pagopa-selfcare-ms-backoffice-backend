package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentServiceProviderDetailsResource extends  PaymentServiceProviderResource{

    @JsonProperty("abi")
    @ApiModelProperty(value = "${swagger.model.pspDetails.abi}")
    private String abi;

    @JsonProperty("bic")
    @ApiModelProperty(value = "${swagger.model.pspDetails.bic}")
    private String bic;

    @JsonProperty("my_bank_code")
    @ApiModelProperty(value = "${swagger.model.pspDetails.myBankCode}")
    private String myBankCode;

    @JsonProperty("stamp")
    @ApiModelProperty(value = "${swagger.model.pspDetails.stamp}")
    private Boolean stamp;

    @JsonProperty("agid_psp")
    @ApiModelProperty(value = "${swagger.model.pspDetails.agidPsp}")
    private Boolean agidPsp = false;

    @JsonProperty("tax_code")
    @ApiModelProperty(value = "${swagger.model.pspDetails.taxCode}")
    private String taxCode;

    @JsonProperty("vat_number")
    @ApiModelProperty(value = "${swagger.model.pspDetails.vatNumber}")
    private String vatNumber;
}

