package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.AwsSesConnector;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, SystemStubsExtension.class})
@ContextConfiguration(classes = {AwsSesServiceImpl.class})
 class AwsSesServiceImplTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Autowired
    private AwsSesServiceImpl awsSesService;

    @MockBean
    private AwsSesConnector awsSesConnector;


    @Test
    void sendEmail_Success() {
        //given
        String to = "to";
        String subject = "subject";
        String body = "body";
        String messageId = "111";
        String res = "Email sent! Message ID: " + messageId;

        when(awsSesConnector.sendEmail(to, subject, body))
                .thenReturn(res);

        //when
        String response = awsSesService.sendEmail(to, subject, body);

        assertEquals(response,res);
        verify(awsSesConnector, times(1))
                .sendEmail(to, subject, body);

        verifyNoMoreInteractions(awsSesConnector);
    }
}
