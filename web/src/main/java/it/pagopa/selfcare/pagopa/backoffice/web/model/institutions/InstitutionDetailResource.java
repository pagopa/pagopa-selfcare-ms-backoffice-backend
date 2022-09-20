package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class InstitutionDetailResource {
    @ApiModelProperty(value = "${swagger.model.institution.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;
    @ApiModelProperty(value = "${swagger.model.institution.externalId}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String externalId;
    @ApiModelProperty(value = "${swagger.model.institution.originId}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String originId;
    @ApiModelProperty(value = "${swagger.model.institution.description}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;
    @ApiModelProperty(value = "${swagger.model.institution.digitalAddress}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String digitalAddress;
    @ApiModelProperty(value = "${swagger.model.institution.address}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String address;
    @ApiModelProperty(value = "${swagger.model.institution.zipCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String zipCode;
    @ApiModelProperty(value = "${swagger.model.institution.taxCode}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String taxCode;
    @ApiModelProperty(value = "${swagger.model.institution.origin}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String origin;
    @ApiModelProperty(value = "${swagger.model.institution.institutionType}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private InstitutionType institutionType;
    @ApiModelProperty(value = "${swagger.model.institution.attributes}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private List<AttributeResource> attributes;
    
}
