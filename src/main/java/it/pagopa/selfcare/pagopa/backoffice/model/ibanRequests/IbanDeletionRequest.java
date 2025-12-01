package it.pagopa.selfcare.pagopa.backoffice.model.ibanRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanDeletionRequest {

    @Schema(description = "Unique identifier of the deletion request")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Creditor institution code")
    @JsonProperty("ciCode")
    private String ciCode;

    @Schema(
            description = "IBAN identification value",
            example = "IT0000000000001000000123456",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("ibanValue")
    @NotNull(message = "IBAN value is required")
    private String ibanValue;

    @Schema(
            description = "Scheduled date for IBAN deletion execution",
            example = "2025-12-31",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("scheduledExecutionDate")
    @NotNull(message = "Scheduled execution date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String scheduledExecutionDate;

    @Schema(description = "Current status of the deletion request")
    @JsonProperty("status")
    private String status;
}