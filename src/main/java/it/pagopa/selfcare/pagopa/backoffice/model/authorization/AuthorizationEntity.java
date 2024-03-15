package it.pagopa.selfcare.pagopa.backoffice.model.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that represent an authorization entity which is the resource identifier that define which objects
 * the entity is authorized to operate on
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationEntity {

    private String name;
    private String value;
    private List<String> values;
}
