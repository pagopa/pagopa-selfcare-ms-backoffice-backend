package it.pagopa.selfcare.pagopa.backoffice.model.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that represent a key-value map that defines the actual content of the {@link AuthorizationMetadata}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationGenericKeyValue {

    private String key;
    private String value;
    private List<String> values;
}
