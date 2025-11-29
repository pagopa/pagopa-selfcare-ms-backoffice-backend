package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanDeletionRequestResponse {

    @Schema(description = "Unique identifier of the deletion task")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Creditor institution code")
    @JsonProperty("ciCode")
    private String ciCode;

    @Schema(description = "IBAN identification value")
    @JsonProperty("ibanValue")
    private String ibanValue;

    @Schema(description = "Scheduled date for IBAN deletion execution")
    @JsonProperty("scheduledExecutionDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledExecutionDate;

    @Schema(description = "Current status of the deletion task")
    @JsonProperty("status")
    private String status;
}