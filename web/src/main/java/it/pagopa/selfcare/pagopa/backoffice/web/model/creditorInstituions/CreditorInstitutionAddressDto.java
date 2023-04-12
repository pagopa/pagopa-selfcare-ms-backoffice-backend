package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreditorInstitutionAddressDto {
    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.location}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String location;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.city}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String city;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.zipCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String zipCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.countryCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String countryCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.taxDomicile}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String taxDomicile;
}
