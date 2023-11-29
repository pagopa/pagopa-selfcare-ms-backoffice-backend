package it.pagopa.selfcare.pagopa.backoffice.model.taxonomies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "Creditor institution type code", required = true)
    @JsonProperty(value = "ci_type_code", required = true)
    @NotBlank
    private String ecTypeCode;

    @ApiModelProperty(value = "Creditor Institution type", required = true)
    @JsonProperty(value = "ci_type", required = true)
    @NotBlank
    private String ecType;


    @ApiModelProperty(value = "Macro Area Progressive", required = true)
    @JsonProperty(value = "macro_area_ci_progressive", required = true)
    @NotBlank
    private String macroAreaEcProgressive;

    @ApiModelProperty(value = "Macro area name", required = true)
    @JsonProperty(value = "macro_area_name", required = true)
    @NotBlank
    private String macroAreaName;

    @ApiModelProperty(value = "Macro area description", required = true)
    @JsonProperty(value = "macro_area_description", required = true)
    @NotBlank
    private String macroAreaDescription;

    @ApiModelProperty(value = "Service type code", required = true)
    @JsonProperty(value = "service_type_code", required = true)
    @NotBlank
    private String serviceTypeCode;

    @ApiModelProperty(value = "Service type", required = true)
    @JsonProperty(value = "service_type", required = true)
    @NotBlank
    private String serviceType;

    @ApiModelProperty(value = "Legal reason for collection", required = true)
    @JsonProperty(value = "legal_reason_collection", required = true)
    @NotBlank
    private String legalReasonCollection;

    @ApiModelProperty(value = "Service description", required = true)
    @JsonProperty(value = "service_type_description", required = true)
    @NotBlank
    private String serviceTypeDescription;

    @ApiModelProperty(value = "Taxonomy version", required = true)
    @JsonProperty(value = "taxonomy_version", required = true)
    @NotBlank
    private String taxonomyVersion;

    @ApiModelProperty(value = "Specific collection data", required = true)
    @JsonProperty(value = "specific_built_in_data", required = true)
    @NotBlank
    private String specificBuiltInData;

    @ApiModelProperty(value = "Start date of validity", required = true)
    @JsonProperty(value = "start_date", required = true)
    @NotBlank
    private String startDate;

    @ApiModelProperty(value = "End date of validity", required = true)
    @JsonProperty(value = "end_date", required = true)
    @NotBlank
    private String endDate;
}
