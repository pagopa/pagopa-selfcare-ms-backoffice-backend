package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionBase {

    @Schema(description = "Institution's unique internal identifier", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "id", required = true)
    @NotBlank
    private String id;

    @Schema(description = "Institution's name", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "name", required = true)
    @NotBlank
    private String description;

    @Schema(description = "Logged user's roles on product", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "user_product_roles", required = true)
    private List<UserProductRole> userProductRoles;

}
