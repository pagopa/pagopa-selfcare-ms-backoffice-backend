package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyInformationsResource {

    @ApiModelProperty(value = "${swagger.institution.model.companyInformations.rea}")
    private String rea;

    @ApiModelProperty(value = "${swagger.institution.model.companyInformations.shareCapital}")
    private String shareCapital;

    @ApiModelProperty(value = "${swagger.institution.model.companyInformations.businessRegisterPlace}")
    private String businessRegisterPlace;

}
