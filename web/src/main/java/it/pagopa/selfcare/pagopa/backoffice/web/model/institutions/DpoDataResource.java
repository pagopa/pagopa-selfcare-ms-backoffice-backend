package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class DpoDataResource {

    @ApiModelProperty(value = "${swagger.institution.model.pspData.dpoData.address}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String address;

    @ApiModelProperty(value = "${swagger.institution.model.pspData.dpoData.pec}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    @Email
    private String pec;

    @ApiModelProperty(value = "${swagger.institution.model.pspData.dpoData.email}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    @Email
    private String email;

}
