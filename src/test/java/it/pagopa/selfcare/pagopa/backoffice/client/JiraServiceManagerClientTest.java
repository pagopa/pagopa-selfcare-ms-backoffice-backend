package it.pagopa.selfcare.pagopa.backoffice.client;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import io.atlassian.util.concurrent.Promise;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = JiraServiceManagerClient.class)
class JiraServiceManagerClientTest {

    @MockBean
    private JiraRestClient jiraRestClient;

    @Autowired
    private JiraServiceManagerClient sut;

    @Test
    void createTicketSuccessPROD() {
        ReflectionTestUtils.setField(sut, "env", "prod");
        ReflectionTestUtils.setField(sut, "projectKey", "project");
        ReflectionTestUtils.setField(sut, "reqTypeTaskId", "12345");

        IssueRestClient mockedClient = mock(IssueRestClient.class);
        when(jiraRestClient.getIssueClient()).thenReturn(mockedClient);
        Promise<BasicIssue> mockedPromise = mock(Promise.class);
        when(mockedClient.createIssue(any())).thenReturn(mockedPromise);
        BasicIssue mockedIssue = mock(BasicIssue.class);
        when(mockedPromise.claim()).thenReturn(mockedIssue);
        when(mockedIssue.getKey()).thenReturn("key");

        assertDoesNotThrow(() -> sut.createTicket("summary", "description"));
    }

    @Test
    void createTicketSuccessUAT() {
        ReflectionTestUtils.setField(sut, "env", "uat");
        ReflectionTestUtils.setField(sut, "projectKey", "project");
        ReflectionTestUtils.setField(sut, "reqTypeTaskId", "12345");

        IssueRestClient mockedClient = mock(IssueRestClient.class);
        when(jiraRestClient.getIssueClient()).thenReturn(mockedClient);
        Promise<BasicIssue> mockedPromise = mock(Promise.class);
        when(mockedClient.createIssue(any())).thenReturn(mockedPromise);
        BasicIssue mockedIssue = mock(BasicIssue.class);
        when(mockedPromise.claim()).thenReturn(mockedIssue);
        when(mockedIssue.getKey()).thenReturn("key");

        assertDoesNotThrow(() -> sut.createTicket("summary", "description"));
    }

    @Test
    void createTicketFailDEV() {
        ReflectionTestUtils.setField(sut, "env", "dev");
        ReflectionTestUtils.setField(sut, "projectKey", "project");
        ReflectionTestUtils.setField(sut, "reqTypeTaskId", "12345");

        assertDoesNotThrow(() -> sut.createTicket("summary", "description"));

        verify(jiraRestClient, never()).getIssueClient();
    }

    @Test
    void createTicketFailLOCAL() {
        ReflectionTestUtils.setField(sut, "env", "local");
        ReflectionTestUtils.setField(sut, "projectKey", "project");
        ReflectionTestUtils.setField(sut, "reqTypeTaskId", "12345");

        assertDoesNotThrow(() -> sut.createTicket("summary", "description"));

        verify(jiraRestClient, never()).getIssueClient();
    }

    @Test
    void createTicketFail() {
        ReflectionTestUtils.setField(sut, "env", "uat");
        ReflectionTestUtils.setField(sut, "projectKey", "project");
        ReflectionTestUtils.setField(sut, "reqTypeTaskId", "12345");

        IssueRestClient mockedClient = mock(IssueRestClient.class);
        when(jiraRestClient.getIssueClient()).thenReturn(mockedClient);
        when(mockedClient.createIssue(any())).thenThrow(RestClientException.class);

        assertDoesNotThrow(() -> sut.createTicket("summary", "description"));
    }
}