package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum RoleType {

    EC("Ente Creditore", List.of("PA", "GSP", "SCP", "PG")),
    PSP("Payment Service Provider", List.of("PSP")),
    PT("Partner Tecnologico", List.of("PT")),
    OTHER("Ignored Role Types", List.of("AS", "SA", "REC", "CON"));

    private final String value;
    private final List<String> selfcareRole;


    public static RoleType fromSelfcareRole(String role) {
        if(role == null) {
            throw new AppException(AppError.SELFCARE_ROLE_NOT_FOUND, "null");
        }
        return Arrays.stream(RoleType.values())
                .filter(elem -> elem.selfcareRole.contains(role))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.SELFCARE_ROLE_NOT_FOUND, role));
    }

}
