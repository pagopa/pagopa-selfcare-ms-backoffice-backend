package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanDeletionRequestUpdateRequest {

    @Schema(
            description = "New scheduled date for IBAN deletion execution",
            example = "2025-12-31",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("scheduledExecutionDate")
    @NotNull(message = "Scheduled execution date is required")
    @Future(message = "Scheduled execution date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledExecutionDate;
}