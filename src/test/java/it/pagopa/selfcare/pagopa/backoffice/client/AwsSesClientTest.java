package it.pagopa.selfcare.pagopa.backoffice.client;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.MessageRejectedException;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AwsSesClient.class)
class AwsSesClientTest {

    @MockBean
    private SesClient sesClient;

    @MockBean
    private SpringTemplateEngine templateEngine;

    @Autowired
    private AwsSesClient sut;

    @Test
    void sendEmailSuccess() {
        when(templateEngine.process(anyString(), any())).thenReturn("html template");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(SendEmailResponse.builder().build());

        String result = assertDoesNotThrow(() -> sut.sendEmail(
                "subject",
                "textBody",
                "htmlTemplateFile.html",
                new Context(),
                "destination"
        ));

        assertNotNull(result);
        assertTrue(result.contains("Email sent!"));
    }

    @Test
    void sendEmailError() {
        when(templateEngine.process(anyString(), any())).thenReturn("html template");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(MessageRejectedException.class);

        String result = assertDoesNotThrow(() -> sut.sendEmail(
                "subject",
                "textBody",
                "htmlTemplateFile.html",
                new Context(),
                "destination"
        ));

        assertNotNull(result);
        assertTrue(result.contains("sendEmail error"));
    }
}