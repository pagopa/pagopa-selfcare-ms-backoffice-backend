package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BundleOffers {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Valid
    private List<PspBundleOffer> offers;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Valid
    private PageInfo pageInfo;
}
