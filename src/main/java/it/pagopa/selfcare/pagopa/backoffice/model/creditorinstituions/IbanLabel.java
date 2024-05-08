package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class IbanLabel {

    @Schema(description = "Label name",requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Label description",requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;
}
