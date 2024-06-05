package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CiBundles {

    @JsonProperty("bundles")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Valid
    private List<CiBundleDetails> bundleDetailsList;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Valid
    private PageInfo pageInfo;
}
