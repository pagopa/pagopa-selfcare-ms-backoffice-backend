package it.pagopa.selfcare.pagopa.backoffice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "whitelist")
public class WhitelistConfig {
    private String urls;

    public String[] getUrlsArray() {
        return urls != null ? urls.split("\\s*,\\s*") : new String[0];
    }

    public boolean isAllowed(String logoUrl) {
        if (logoUrl == null || logoUrl.isEmpty()) {
            return true;
        }
        return Arrays.stream(getUrlsArray())
                .anyMatch(logoUrl::startsWith);
    }
}
