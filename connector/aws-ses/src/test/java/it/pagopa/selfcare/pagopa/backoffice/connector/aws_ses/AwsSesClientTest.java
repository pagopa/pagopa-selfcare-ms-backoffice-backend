package it.pagopa.selfcare.pagopa.backoffice.connector.aws_ses;


import it.pagopa.selfcare.pagopa.backoffice.connector.api.AwsSesConnector;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import java.net.URISyntaxException;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {
        AwsSesClient.class})
@ExtendWith(SpringExtension.class)
class AwsSesClientTest {


    @MockBean
    private SesClient sesClient;

    @MockBean
    private AwsSesConnector awsSesConnector;


    @Test
    void sendEmail_Success() throws URISyntaxException {
        //given
        String from = "from";
        String to = "to";
        String subject = "subject";
        String body = "body";
        String messageId = "111";

        SendEmailRequest request = SendEmailRequest.builder()
                .source(from)
                .destination(Destination.builder().toAddresses(to).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder().text(Content.builder().data(body).build()).build())
                        .build())
                .build();

        SendEmailResponse res = SendEmailResponse.builder().messageId(messageId).build();
        when(sesClient.sendEmail(eq(request))).thenReturn(res);

        //when
        String response = awsSesConnector.sendEmail(to, subject, body);

                verify(awsSesConnector, times(1))
                .sendEmail(eq(to),eq(subject),eq(body));

        when(awsSesConnector.sendEmail(eq(to),eq(subject),eq(body)))
                .thenReturn("Email sent! Message ID: " + messageId);
    }

}