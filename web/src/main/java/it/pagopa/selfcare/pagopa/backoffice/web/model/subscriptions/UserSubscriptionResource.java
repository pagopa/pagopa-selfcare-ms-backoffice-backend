package it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserSubscriptionResource {
    
    @ApiModelProperty(value = "${swagger.institutions.model.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;
    @ApiModelProperty(value = "${swagger.institutions.model.name}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "${swagger.institutions.model.primaryKey}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String primaryKey;
    @ApiModelProperty(value = "${swagger.institutions.model.secondaryKey}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String secondaryKey;
    
}
