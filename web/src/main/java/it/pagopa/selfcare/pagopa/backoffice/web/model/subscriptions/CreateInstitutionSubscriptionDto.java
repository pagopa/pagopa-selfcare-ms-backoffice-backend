package it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CreateInstitutionSubscriptionDto {
    
    @ApiModelProperty("${swagger.institutions.model.name}")
    @JsonProperty(required = true)
    @NotBlank
    private String description;
    @ApiModelProperty("${swagger.institutions.model.externalId}")
    @JsonProperty(required = true)
    @NotBlank
    private String externalId;
    @ApiModelProperty("${swagger.institutions.model.email}")
    @JsonProperty(required = true)
    @NotBlank
    @Email
    private String email;
    
}
