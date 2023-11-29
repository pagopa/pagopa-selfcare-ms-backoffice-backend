package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class SupportContact {

    private String supportEmail;

    private String supportPhone;
}
