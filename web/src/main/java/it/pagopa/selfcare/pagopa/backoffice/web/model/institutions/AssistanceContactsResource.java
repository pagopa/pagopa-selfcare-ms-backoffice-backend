package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class AssistanceContactsResource {

    @ApiModelProperty(value = "${swagger.institution.model.assistance.supportEmail}")
    @Email
    private String supportEmail;

    @ApiModelProperty(value = "${swagger.institution.model.assistance.supportPhone}")
    private String supportPhone;

}
