package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AzureApiManagerConfig.class)
public class AzureApiManagerTestConfig {
}
