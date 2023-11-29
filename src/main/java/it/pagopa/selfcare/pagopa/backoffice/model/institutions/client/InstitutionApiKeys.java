package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InstitutionApiKeys {


    @ApiModelProperty(value = "Institution's subscription id", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "Institution's name Api Key", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String displayName;

    @ApiModelProperty(value = "Institution's primary Api Key", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String primaryKey;

    @ApiModelProperty(value = "Institution's secondary Api Key", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String secondaryKey;
}
