package it.pagopa.selfcare.pagopa.backoffice.config.azure;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureCredential {

    @Value("${azure.resource-manager.api-manager.client-id}")
    private String clientId;

    @Value("${azure.resource-manager.api-manager.client-secret}")
    private String clientSecret;

    @Value("${azure.resource-manager.api-manager.tenant-id}")
    private String tenantId;

    @Bean
    public ClientSecretCredential createCredential() {
        return new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
    }

}
