package it.pagopa.selfcare.pagopa.backoffice.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TavoloOpResource {


    @Schema(description = "Fiscal code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String taxCode;

    @Schema(description = "Psp",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String name;

    @Schema(description = "referent",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String referent;

    @Schema(description = "contact person's email address",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String email;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String telephone;

    @Schema(description = "Date of update",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private Instant modifiedAt;

    @Schema(description = "person who made the change",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String modifiedBy;

    @Schema(description = "Date of insert")
    private Instant createdAt;

    @Schema(description = "Person who made the change",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    private String createdBy;

}
