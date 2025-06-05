package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import java.util.Collections;
import java.util.List;

public enum ProductRole {

    ADMIN(List.of("admin", "admin-psp")),
    OPERATOR(List.of("operator", "operator-psp")),
    PAGOPA_OPERATOR(Collections.emptyList()),
    ALL(Collections.emptyList());

    private final List<String> value;

    ProductRole(List<String> value) {
        this.value = value;
    }

    public List<String> getValue() {
        return value;
    }
}
