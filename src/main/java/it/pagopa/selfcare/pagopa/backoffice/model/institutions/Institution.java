package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Institution {

    @ApiModelProperty(value = "Institution's unique internal identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "Institution's unique external identifier", required = true)
    @JsonProperty(value = "external_id", required = true)
    @NotBlank
    private String externalId;

    @ApiModelProperty(value = "Institution's details origin Id", required = true)
    @JsonProperty(value = "origin_id", required = true)
    @NotBlank
    private String originId;

    @ApiModelProperty(value = "Institution's name", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(value = "Institution's digitalAddress")
    @JsonProperty(value = "digital_address")
    private String digitalAddress;

    @ApiModelProperty(value = "Institution's physical address")
    @JsonProperty()
    private String address;

    @ApiModelProperty(value = "Institution's zipCode", required = true)
    @JsonProperty(value = "zip_code", required = true)
    @NotBlank
    private String zipCode;

    @ApiModelProperty(value = "Institution's taxCode", required = true)
    @JsonProperty(value = "tax_code", required = true)
    @NotBlank
    private String taxCode;

    @ApiModelProperty(value = "Institution data origin", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String origin;

    @ApiModelProperty(value = "Institution's type", required = true)
    @JsonProperty(value = "institution_type", required = true)
    @NotNull
    private InstitutionType institutionType;

    @ApiModelProperty(value = "Institution's attributes", required = true)
    @JsonProperty(required = true)
    @NotNull
    private List<Attribute> attributes;
}
