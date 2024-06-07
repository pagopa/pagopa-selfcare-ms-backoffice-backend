package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;

@AllArgsConstructor
public enum RoleType {

    CI("Ente Creditore", List.of("PA", "GSP", "SCP", "PG")),
    PSP("Payment Service Provider", List.of("PSP")),
    PT("Partner Tecnologico", List.of("PT")),
    OTHER("Ignored Role Types", List.of("AS", "SA", "REC", "CON"));

    private final String value;
    private final List<String> selfcareRole;


    public static RoleType fromSelfcareRole(String institutionCode, String role) {
        return Arrays.stream(RoleType.values())
                .filter(elem -> role != null && elem.selfcareRole.contains(role))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.SELFCARE_ROLE_NOT_FOUND, deNull(role), deNull(institutionCode)));
    }

}
