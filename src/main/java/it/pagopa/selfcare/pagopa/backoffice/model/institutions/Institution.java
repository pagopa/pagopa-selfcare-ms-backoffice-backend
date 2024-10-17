package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Institution {

    @Schema(description = "Institution's unique internal identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @Schema(description = "Institution's unique external identifier",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "external_id", required = true)
    @NotBlank
    private String externalId;

    @Schema(description = "Institution's details origin Id",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "origin_id", required = true)
    @NotBlank
    private String originId;

    @Schema(description = "Institution's name",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String description;

    @Schema(description = "Institution's digitalAddress")
    @JsonProperty(value = "digital_address")
    private String digitalAddress;

    @Schema(description = "Institution's physical address")
    @JsonProperty()
    private String address;

    @Schema(description = "Institution's zipCode",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "zip_code", required = true)
    @NotBlank
    private String zipCode;

    @Schema(description = "Institution's taxCode",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "tax_code", required = true)
    @NotBlank
    private String taxCode;

    @Schema(description = "Institution data origin",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotBlank
    private String origin;

    @Schema(description = "Institution's type",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "institution_type", required = true)
    @NotNull
    @Valid
    private InstitutionType institutionType;

    @Schema(description = "Institution's attributes",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(required = true)
    @NotNull
    @Valid
    private List<@Valid Attribute> attributes;
}
