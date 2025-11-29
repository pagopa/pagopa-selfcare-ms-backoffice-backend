package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanDeletionRequestResponse {

    @Schema(description = "List of IBAN deletion requests", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("requests")
    private List<IbanDeletionRequest> requests;
}