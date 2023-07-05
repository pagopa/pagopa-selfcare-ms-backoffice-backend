package it.pagopa.selfcare.pagopa.backoffice.connector.rest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@Import(ApiConfigSelfcareIntegrationRestClientConfig.class)
@TestConfiguration
public class ApiConfigSelfcareIntegrationRestClientConfigTest {
}
