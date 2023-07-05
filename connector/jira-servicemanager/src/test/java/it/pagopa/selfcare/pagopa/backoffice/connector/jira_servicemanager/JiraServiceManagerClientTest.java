package it.pagopa.selfcare.pagopa.backoffice.connector.jira_servicemanager;


import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import io.atlassian.util.concurrent.Promises;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.JiraServiceManagerConnector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ContextConfiguration(classes = {

        JiraServiceManagerClient.class
})
@ExtendWith(SpringExtension.class)
class JiraServiceManagerClientTest {



    @MockBean
    private JiraRestClient jiraRestClient;

    @Autowired
    private JiraServiceManagerConnector jiraServiceManagerClient;


    @Test
    void createTicket_Success() throws URISyntaxException {
        // Creazione del mock dell'interfaccia IssueRestClient
        IssueRestClient issueClient = Mockito.mock(IssueRestClient.class);
        jiraServiceManagerClient.setReqTypeTaskId("10000");
        // Configurazione del comportamento del mock
        when(jiraRestClient.getIssueClient()).thenReturn(issueClient);
        when(issueClient.createIssue(any(IssueInput.class))).thenReturn(Promises.promise(new BasicIssue(new URI(""), "TICKET-001", 1L)));

        // Esecuzione del metodo da testare
        String result = jiraServiceManagerClient.createTicket("Summary", "Description");

        // Verifica dei risultati
        assertEquals("Created ticket TICKET-001", result);
    }
}