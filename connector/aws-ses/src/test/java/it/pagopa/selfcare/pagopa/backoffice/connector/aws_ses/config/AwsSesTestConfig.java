package it.pagopa.selfcare.pagopa.backoffice.connector.aws_ses.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AwsSesConfig.class)
public class AwsSesTestConfig {


//    @MockBean
//    public JiraRestClient jiraRestClient;
}
