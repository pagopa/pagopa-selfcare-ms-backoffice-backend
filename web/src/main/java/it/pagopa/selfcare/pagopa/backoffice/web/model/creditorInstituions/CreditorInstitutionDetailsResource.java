package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;

@Data
public class CreditorInstitutionDetailsResource extends CreditorInstitutionResource{


    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address}", required = true)
    @Valid
    private CreditorInstitutionAddressResource address;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingZip}", required = true)
    private Boolean pspPayment;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingZip}", required = true)
    private Boolean reportingFtp;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingZip}", required = true)
    private Boolean reportingZip;
}
