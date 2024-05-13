package it.pagopa.selfcare.pagopa.backoffice.model.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Model class that contains the maintenance messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceMessage {

    @Schema(description = "Maintenance banner message",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String bannerMessage;

    @Schema(description = "Maintenance page message",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String pageMessage;
}
