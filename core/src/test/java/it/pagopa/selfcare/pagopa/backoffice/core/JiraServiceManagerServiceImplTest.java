package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.JiraServiceManagerConnector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, SystemStubsExtension.class})
@ContextConfiguration(classes = {JiraServiceManagerServiceImpl.class})
class JiraServiceManagerServiceImplTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Autowired
    private JiraServiceManagerServiceImpl jiraServiceManagerService;

    @MockBean
    private JiraServiceManagerConnector jiraServiceManagerConnectorMock;


    @Test
    void createTicket() {
        //given
        String summary = "institutionId";
        String description = "description";
        String responseMock = "";
        //when
        String response = jiraServiceManagerService.createTicket(summary,description);
        //then
        assertEquals(response, responseMock);
        verify(jiraServiceManagerConnectorMock, times(1))
                .createTicket(summary,description);
        verifyNoMoreInteractions(jiraServiceManagerConnectorMock);

    }

}
