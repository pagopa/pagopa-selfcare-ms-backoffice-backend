package it.pagopa.selfcare.pagopa.backoffice.connector.rest.config;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@Import(ApiConfigRestClientConfig.class)
@TestConfiguration
public class ApiConfigRestClientConfigTest {
}
