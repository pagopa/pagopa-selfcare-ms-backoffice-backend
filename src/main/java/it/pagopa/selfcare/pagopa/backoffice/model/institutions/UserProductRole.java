package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProductRole {

    @Schema(description = "Logged user role on product", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = "admin, operator, admin-psp, operator-psp")
    @JsonProperty(value = "product_role", required = true)
    @NotBlank
    @Pattern(regexp = "admin|operator|admin-psp|operator-psp", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String productRole;

    @Schema(description = "Logged user role label")
    @JsonProperty(value = "product_role_label")
    private String productRoleLabel;

}
