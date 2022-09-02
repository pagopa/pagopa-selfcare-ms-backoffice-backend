package it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ApiKeysResource {
    
    @ApiModelProperty(value = "${swagger.model.institution.primaryKey}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String primaryKey;
    @ApiModelProperty(value = "${swagger.model.institution.secondaryKey}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String secondaryKey;
    
}
