package it.pagopa.selfcare.pagopa.backoffice.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserSubscriptionResource {
    
    @ApiModelProperty(value = "${swagger.pagopa.backoffice.institutions.model.id}")
    @JsonProperty(required = true)
    @NotBlank
    private String id;
    @ApiModelProperty(value = "${swagger.pagopa.backoffice.institutions.model.name}")
    @JsonProperty(required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "${swagger.pagopa.backoffice.institutions.model.primaryKey}")
    @JsonProperty(required = true)
    @NotBlank
    private String primaryKey;
    @ApiModelProperty(value = "${swagger.pagopa.backoffice.institutions.model.secondaryKey}")
    @JsonProperty(required = true)
    @NotBlank
    private String secondaryKey;
    
}
