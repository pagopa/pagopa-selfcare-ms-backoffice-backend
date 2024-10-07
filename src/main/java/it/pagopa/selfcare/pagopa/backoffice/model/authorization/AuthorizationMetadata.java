package it.pagopa.selfcare.pagopa.backoffice.model.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that represent the authorization metadata
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationMetadata {

    private String name;

    @JsonProperty("short_key")
    private String shortKey;

    private List<AuthorizationGenericKeyValue> content;
}
