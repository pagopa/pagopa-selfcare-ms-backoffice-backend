package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class CreditorInstitutionDetailsResource extends CreditorInstitutionResource{


    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address}", required = true)
    @NotNull
    @Valid
    private CreditorInstitutionAddressResource address;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingZip}", required = true)
    @NotNull
    private Boolean pspPayment;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingZip}", required = true)
    @NotNull
    private Boolean reportingFtp;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.reportingZip}", required = true)
    @NotNull
    private Boolean reportingZip;
}
