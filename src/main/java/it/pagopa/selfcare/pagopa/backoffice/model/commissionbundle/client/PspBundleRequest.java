package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PspBundleRequest {

    @JsonProperty("idBundleRequest")
    @NotNull
    private String id;

    @NotBlank
    private String idBundle;

    @NotNull
    private String ciFiscalCode;

    private LocalDateTime acceptedDate;

    private LocalDateTime rejectionDate;

    private List<PspCiBundleAttribute> ciBundleAttributes;
}
