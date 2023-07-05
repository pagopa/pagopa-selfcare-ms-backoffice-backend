package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionAddressResource {
    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.location}", required = true)
    private String location;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.city}", required = true)
    private String city;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.zipCode}", required = true)
    private String zipCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.countryCode}", required = true)
    private String countryCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.taxDomicile}", required = true)
    private String taxDomicile;
}
