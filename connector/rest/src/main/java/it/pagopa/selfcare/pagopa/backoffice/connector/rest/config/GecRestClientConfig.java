package it.pagopa.selfcare.pagopa.backoffice.connector.rest.config;

import it.pagopa.selfcare.pagopa.backoffice.connector.rest.client.ApiConfigRestClient;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.client.GecRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(RestClientBaseConfig.class)
@EnableFeignClients(clients = GecRestClient.class)
@PropertySource("classpath:config/gec-rest-client.properties")
public class GecRestClientConfig {
}
