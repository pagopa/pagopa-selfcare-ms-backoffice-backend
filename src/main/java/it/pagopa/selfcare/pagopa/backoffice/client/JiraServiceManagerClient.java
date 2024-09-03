package it.pagopa.selfcare.pagopa.backoffice.client;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import it.pagopa.selfcare.pagopa.backoffice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JiraServiceManagerClient {

    private final JiraRestClient jiraRestClient;

    private final String projectKey;

    private final String reqTypeTaskId;

    private final String env;

    public JiraServiceManagerClient(
            JiraRestClient jiraRestClient,
            @Value("${jira.project.key}") String projectKey,
            @Value("${jira.reqTypeTaskId}") String reqTypeTaskId,
            @Value("${info.properties.environment}") String env
    ) {
        this.jiraRestClient = jiraRestClient;
        this.projectKey = projectKey;
        this.reqTypeTaskId = reqTypeTaskId;
        this.env = env;
    }

    /**
     * Create a ticket with the given summary and description
     *
     * @param summary     the ticket summary
     * @param description the ticket description
     */
    public void createTicket(String summary, String description) {
        if (!this.env.equals("uat") && !this.env.equals("prod")) {
            log.warn("Skip JIRA ticket creation process");
            return;
        }
        log.trace("createTicket start");
        log.debug("createInstitution summary = {}, description = {}", summary, description);
        try {
            // https://sitename.atlassian.net/rest/api/latest/project
            IssueRestClient issueClient = this.jiraRestClient.getIssueClient();

            String ticketSummary = String.format("[BOpagopa] [%s] %s", env, summary);
            IssueInput newIssue = new IssueInputBuilder(this.projectKey, Long.parseLong(this.reqTypeTaskId), ticketSummary)
                    .setFieldValue("description", description)
                    .build();

            String ticketId = issueClient.createIssue(newIssue).claim().getKey();

            log.debug(Constants.CONFIDENTIAL_MARKER, "createTicket number = {}", ticketId);
            log.trace("createTicket end");
        } catch (Exception e) {
            log.error("createTicket error", e);
        }
    }
}
