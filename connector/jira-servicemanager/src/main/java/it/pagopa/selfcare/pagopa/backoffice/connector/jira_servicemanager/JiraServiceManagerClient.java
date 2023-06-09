package it.pagopa.selfcare.pagopa.backoffice.connector.jira_servicemanager;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.JiraServiceManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JiraServiceManagerClient implements JiraServiceManagerConnector {
    @Autowired
    private JiraRestClient jiraRestClient;
    @Value("${jira.project.key}")
    private String projectKey;
    @Value("${jira.reqTypeTaskId}")
    private String reqTypeTaskId;

    public String createTicket(String summary, String description) {
        try {
            log.trace("createTicket start");
            log.debug("createInstitution summary = {}, description = {}", summary, description);

          // https://sitename.atlassian.net/rest/api/latest/project
            IssueRestClient issueClient = jiraRestClient.getIssueClient();

            IssueInput newIssue = new IssueInputBuilder(
                    projectKey, Long.parseLong(reqTypeTaskId), summary)
                    .setFieldValue("description", description)
                    .build();

            String ticketId = issueClient.createIssue(newIssue).claim().getKey();

            log.debug(LogUtils.CONFIDENTIAL_MARKER, "createTicket number = {}",ticketId);
            log.trace("createTicket end");
            return String.format("Created ticket %s",ticketId);
        } catch (Exception e) {
            return "createTicket error: " + e.getMessage();
        }
    }
}
