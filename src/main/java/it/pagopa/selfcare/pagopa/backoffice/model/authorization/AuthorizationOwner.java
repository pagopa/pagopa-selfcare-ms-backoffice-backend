package it.pagopa.selfcare.pagopa.backoffice.model.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that holds the information about the owner of the authorization
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationOwner {

    private String id;
    private String name;
    private String type;
}
