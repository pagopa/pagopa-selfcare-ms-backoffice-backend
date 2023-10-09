package it.pagopa.selfcare.pagopa.backoffice.web.model.taxonomies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TaxonomyResource {


    @ApiModelProperty(value = "${swagger.model.taxonomy.ecTypeCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String ecTypeCode;

    @ApiModelProperty(value = "${swagger.model.taxonomy.ecType}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String ecType;


    @ApiModelProperty(value = "${swagger.model.taxonomy.macroAreaEcProgressive}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String macroAreaEcProgressive;

    @ApiModelProperty(value = "${swagger.model.taxonomy.macroAreaName}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String macroAreaName;

    @ApiModelProperty(value = "${swagger.model.taxonomy.macroAreaDescription}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String macroAreaDescription;

    @ApiModelProperty(value = "${swagger.model.taxonomy.serviceTypeCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String serviceTypeCode;

    @ApiModelProperty(value = "${swagger.model.taxonomy.serviceType}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String serviceType;

    @ApiModelProperty(value = "${swagger.model.taxonomy.legalReasonCollection}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String legalReasonCollection;

    @ApiModelProperty(value = "${swagger.model.taxonomy.serviceTypeDescription}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String serviceTypeDescription;

    @ApiModelProperty(value = "${swagger.model.taxonomy.taxonomyVersion}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String taxonomyVersion;

    @ApiModelProperty(value = "${swagger.model.taxonomy.specificBuiltInData}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String specificBuiltInData;

    @ApiModelProperty(value = "${swagger.model.taxonomy.startDate}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String startDate;

    @ApiModelProperty(value = "${swagger.model.taxonomy.endDate}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String endDate;


}
