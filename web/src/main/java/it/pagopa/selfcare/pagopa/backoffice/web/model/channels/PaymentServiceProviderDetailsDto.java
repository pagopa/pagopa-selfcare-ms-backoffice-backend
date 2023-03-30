package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentServiceProviderDetailsDto{

    @JsonProperty("abi")
    private String abi;

    @JsonProperty("bic")
    private String bic;

    @JsonProperty("transfer")
    private Boolean transfer;

    @JsonProperty("my_bank_code")
    private String myBankCode;

    @JsonProperty("stamp")
    private Boolean stamp;

    @JsonProperty("agid_psp")
    private Boolean agidPsp = false;

    @JsonProperty("tax_code")
    private String taxCode;

    @JsonProperty("vat_number")
    private String vatNumber;

    @JsonProperty("psp_code")
    private String pspCode;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("business_name")
    private String businessName;

}
