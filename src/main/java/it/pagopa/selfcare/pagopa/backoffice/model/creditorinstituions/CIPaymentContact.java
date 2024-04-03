package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "User's unique identifier")
    @JsonProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "User's email")
    @JsonProperty(value = "email")
    private String email;

    @ApiModelProperty(value = "User's name")
    @JsonProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "User's surname")
    @JsonProperty(value = "surname")
    private String surname;

    @ApiModelProperty(value = "User's tax code")
    @JsonProperty(value = "user_tax_code")
    private String fiscalCode;

    @ApiModelProperty(value = "User's roles")
    @JsonProperty(value = "roles")
    private List<String> roles;
}
