package it.pagopa.selfcare.pagopa.backoffice.web.model.channels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.commons.nullanalysis.NotNull;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PaymentServiceProviderDetailsDto{

    public PaymentServiceProviderDetailsDto(String pspCode, Boolean enabled, String businessName){
        this.pspCode = pspCode;
        this.enabled = enabled;
        this.businessName = businessName;
    }

    @JsonProperty(value = "psp_code", required = true)
    @NotNull
    private String pspCode;

    @JsonProperty(value = "enabled", required = true)
    @NotNull
    private Boolean enabled;

    @JsonProperty(value = "business_name", required = true)
    @NotNull
    private String businessName;

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
