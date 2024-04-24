package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum that contains selfcare product users
 */
@AllArgsConstructor
@Getter
public enum SelfcareProductUser {

    ADMIN("admin"),
    OPERATOR("operator");

    private final String productUser;
}
