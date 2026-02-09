package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceConsent;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ServiceId;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "institution.services")
public class InstitutionServicesConfig {
    @NotEmpty
    private Map<ServiceId, ServiceConsent> defaultConsents = new EnumMap<>(ServiceId.class);

    @PostConstruct
    public void validateDefaultConsents() {
        List<ServiceId> notConfiguredKeys = Arrays.stream(ServiceId.values())
                .filter(key -> key != ServiceId.UNKNOWN)
                .filter(key -> !defaultConsents.containsKey(key))
                .toList();

        if (!notConfiguredKeys.isEmpty()) {
            throw new IllegalStateException(
                    "Misconfigured Institution services mapping. Missing keys: " + notConfiguredKeys
            );
        }

        log.info("Institution services default consents mapped correctly: {}", defaultConsents);
    }
}
