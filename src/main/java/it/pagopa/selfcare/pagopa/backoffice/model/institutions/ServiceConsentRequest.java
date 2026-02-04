package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConsentRequest {

    @Schema(description = "The expressed consent", example = "OPT_OUT")
    @JsonProperty(value = "consent")
    @NotNull
    private ServiceConsent consent;
}
