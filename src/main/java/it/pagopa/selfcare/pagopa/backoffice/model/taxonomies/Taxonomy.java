package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Taxonomy {

    @Schema(description = "Creditor institution type code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ci_type_code", required = true)
    @NotBlank
    private String ecTypeCode;

    @Schema(description = "Creditor Institution type",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ci_type", required = true)
    @NotBlank
    private String ecType;


    @Schema(description = "Macro Area Progressive",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "macro_area_ci_progressive", required = true)
    @NotBlank
    private String macroAreaEcProgressive;

    @Schema(description = "Macro area name",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "macro_area_name", required = true)
    @NotBlank
    private String macroAreaName;

    @Schema(description = "Macro area description",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "macro_area_description", required = true)
    @NotBlank
    private String macroAreaDescription;

    @Schema(description = "Service type code",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "service_type_code", required = true)
    @NotBlank
    private String serviceTypeCode;

    @Schema(description = "Service type",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "service_type", required = true)
    @NotBlank
    private String serviceType;

    @Schema(description = "Legal reason for collection",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "legal_reason_collection", required = true)
    @NotBlank
    private String legalReasonCollection;

    @Schema(description = "Service description",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "service_type_description", required = true)
    @NotBlank
    private String serviceTypeDescription;

    @Schema(description = "Taxonomy version",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "taxonomy_version", required = true)
    @NotBlank
    private String taxonomyVersion;

    @Schema(description = "Specific collection data",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "specific_built_in_data", required = true)
    @NotBlank
    private String specificBuiltInData;

    @Schema(description = "Start date of validity",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "start_date", required = true)
    @NotBlank
    private String startDate;

    @Schema(description = "End date of validity",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "end_date", required = true)
    @NotBlank
    private String endDate;
}
