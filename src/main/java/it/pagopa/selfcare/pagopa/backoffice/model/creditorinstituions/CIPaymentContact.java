package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that represent a creditor institution's payment contact
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CIPaymentContact {

    @Schema(description = "User's unique identifier")
    @JsonProperty(value = "id")
    private String id;

    @Schema(description = "User's email")
    @JsonProperty(value = "email")
    private String email;

    @Schema(description = "User's name")
    @JsonProperty(value = "name")
    private String name;

    @Schema(description = "User's surname")
    @JsonProperty(value = "surname")
    private String surname;

    @Schema(description = "User's tax code")
    @JsonProperty(value = "user_tax_code")
    private String fiscalCode;

    @Schema(description = "User's roles")
    @JsonProperty(value = "roles")
    private List<String> roles;
}
