package it.pagopa.selfcare.pagopa.backoffice.connector.jira_servicemanager.config;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.URI;
import java.net.URISyntaxException;

@PropertySource("classpath:config/jira-servicemanager-config.properties")
@Configuration
public class JiraServiceManagerConfig {

    @Value("${jira.url}")
    private String jiraUrl;

    @Value("${jira.username}")
    private String username;

    @Value("${jira.password}")
    private String password;

    @Bean
    public JiraRestClient jiraRestClient() throws URISyntaxException {
        return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication( URI.create(jiraUrl), username, password);
    }
}
