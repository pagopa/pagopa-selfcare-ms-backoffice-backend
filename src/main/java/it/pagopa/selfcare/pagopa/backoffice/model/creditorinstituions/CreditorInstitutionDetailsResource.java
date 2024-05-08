package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;

@Data
public class CreditorInstitutionDetailsResource extends CreditorInstitutionResource {


    @Schema(description = "Creditor Institution's address object",requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private CreditorInstitutionAddressResource address;

    @Schema(description = "Enables the zipping of the content that goes through fstp",requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean pspPayment;

    @Schema(description = "Enables the zipping of the content that goes through fstp",requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean reportingFtp;

    @Schema(description = "Enables the zipping of the content that goes through fstp",requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean reportingZip;
}
