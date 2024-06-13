package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleOffered {

    @JsonProperty("ciFiscalCode")
    private String ciTaxCode;

    private String idBundleOffer;
}