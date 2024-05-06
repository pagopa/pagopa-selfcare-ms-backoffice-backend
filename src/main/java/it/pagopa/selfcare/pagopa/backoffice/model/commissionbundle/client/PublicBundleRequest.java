package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicBundleRequest {

    @JsonProperty("idBundleRequest")
    @NotNull
    private String id;
    @NotBlank
    private String idBundle;
    private String idPsp;
    @NotNull
    private String ciFiscalCode;

    @Schema(description = "the start date of the bundle if accepted")
    private LocalDate validityDateFrom;

    @Schema(description = "the end date of the bundle if accepted")
    private LocalDate validityDateTo;

    private LocalDateTime acceptedDate;
    private LocalDateTime rejectionDate;
    private LocalDateTime insertedDate;

    @JsonProperty("attributes")
    private List<PspCiBundleAttribute> ciBundleAttributes;
}
