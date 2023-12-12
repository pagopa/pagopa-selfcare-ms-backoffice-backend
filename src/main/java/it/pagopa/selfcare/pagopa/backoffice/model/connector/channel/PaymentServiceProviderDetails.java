package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentServiceProviderDetails extends PaymentServiceProvider {

    @JsonProperty("abi")
    private String abi;

    @JsonProperty("bic")
    private String bic;

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

}
