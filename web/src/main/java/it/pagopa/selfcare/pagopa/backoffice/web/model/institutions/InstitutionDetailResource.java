package it.pagopa.selfcare.pagopa.backoffice.web.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @ApiModelProperty(value = "${swagger.model.institution.name}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;
    @ApiModelProperty(value = "${swagger.model.institution.digitalAddress}")
    @JsonProperty(required = false)
    private String digitalAddress;
    @ApiModelProperty(value = "${swagger.model.institution.address}")
    @JsonProperty(required = false)
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
    @NotNull
    private InstitutionType institutionType;
    @ApiModelProperty(value = "${swagger.model.institution.attributes}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private List<AttributeResource> attributes;
    
}
