package it.pagopa.selfcare.pagopa.backoffice.connector.jira_servicemanager;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.JiraServiceManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JiraServiceManagerClient implements JiraServiceManagerConnector {
    @Autowired
    @Qualifier("jiraRestClient")
    private JiraRestClient jiraRestClient;

    private static final Long REQUEST_TYPE_TASKID = 10001L;
    @Value("${jira.url}")
    private String jiraUrl;

    public String createTicket(String summary, String description) {
        try {
            log.trace("createTicket start");
            log.debug("createInstitution summary = {}, description = {}", summary, description);
            String projectKey = "PROV"; // Sostituisci con la chiave del tuo progetto
          // https://iltestdellasettimana.atlassian.net/rest/api/2/issuetype
            IssueRestClient issueClient = jiraRestClient.getIssueClient();

            IssueInput newIssue = new IssueInputBuilder(
                    projectKey, REQUEST_TYPE_TASKID, summary)
                    .setFieldValue("description", description)
                    .build();

            String ticketId = issueClient.createIssue(newIssue).claim().getKey();

            log.debug(LogUtils.CONFIDENTIAL_MARKER, "createTicket number = {}",ticketId);
            log.trace("createTicket end");
            return ticketId;
        } catch (Exception e) {
            return "createTicket error: " + e.getMessage();
        }
    }
}
