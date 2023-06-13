package it.pagopa.selfcare.pagopa.backoffice.connector.jira_servicemanager.config;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JiraServiceManagerConfig.class)
public class JiraServiceManagerTestConfig {


    @MockBean
    public JiraRestClient jiraRestClient;
}
