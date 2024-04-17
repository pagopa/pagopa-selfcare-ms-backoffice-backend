package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CiFiscalCodeList {

    @JsonProperty("ciFiscalCodeList")
    List<String> ciTaxCodeList;
}
