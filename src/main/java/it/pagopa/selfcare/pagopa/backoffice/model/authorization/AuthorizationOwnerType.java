package it.pagopa.selfcare.pagopa.backoffice.model.authorization;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Enum that defines the authorization owner types
 */
@AllArgsConstructor
public enum AuthorizationOwnerType {

    CI(List.of("PA", "GSP", "SCP", "PG")),
    PSP(List.of("PSP")),
    BROKER(List.of("PT")),
    OTHER(List.of("AS", "SA", "REC", "CON"));

    private final List<String> selfcareRole;

    public static AuthorizationOwnerType fromSelfcareRole(String role) {
        if(role == null) {
            throw new AppException(AppError.SELFCARE_ROLE_NOT_FOUND, "null");
        }
        return Arrays.stream(AuthorizationOwnerType.values())
                .filter(elem -> elem.selfcareRole.contains(role))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.SELFCARE_ROLE_NOT_FOUND, role));
    }
}
