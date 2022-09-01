package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:config/azure-apim-config.properties")
@Configuration
@Profile("AzureAPIManagement")
public class AzureApimConfig {
}
