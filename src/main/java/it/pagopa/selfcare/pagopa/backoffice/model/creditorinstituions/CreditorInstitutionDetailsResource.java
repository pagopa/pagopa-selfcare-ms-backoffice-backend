package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;

@Data
public class CreditorInstitutionDetailsResource extends CreditorInstitutionResource{


    @ApiModelProperty(value = "Creditor Institution's address object", required = true)
    @Valid
    private CreditorInstitutionAddressResource address;

    @ApiModelProperty(value = "Enables the zipping of the content that goes through fstp", required = true)
    private Boolean pspPayment;

    @ApiModelProperty(value = "Enables the zipping of the content that goes through fstp", required = true)
    private Boolean reportingFtp;

    @ApiModelProperty(value = "Enables the zipping of the content that goes through fstp", required = true)
    private Boolean reportingZip;
}
