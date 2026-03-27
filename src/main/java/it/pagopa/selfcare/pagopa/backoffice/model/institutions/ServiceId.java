package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import java.util.Arrays;

/**
 * Enumeration of institution services
 */
public enum ServiceId {
    /**
     * Request to pay
     */
    RTP,
    UNKNOWN;

    public static ServiceId fromString(String text) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(text))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
