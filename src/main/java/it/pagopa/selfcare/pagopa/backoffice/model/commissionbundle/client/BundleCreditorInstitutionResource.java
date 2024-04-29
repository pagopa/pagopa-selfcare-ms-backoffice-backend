package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BundleCreditorInstitutionResource {

    private List<CiBundleDetails> ciBundleDetails;

    @JsonProperty("pageInfo")
    @NotNull
    private PageInfo pageInfo;
}
