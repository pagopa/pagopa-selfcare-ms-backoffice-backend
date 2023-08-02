package it.pagopa.selfcare.pagopa.backoffice.connector.aws_ses;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.AwsSesConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Slf4j
@Service
public class AwsSesClient implements AwsSesConnector {

    @Autowired
    private SesClient sesClient;

    @Value("${aws.ses.user}")
    private String from;

    public String sendEmail(String subject, String body, String...to) {

        SendEmailRequest request = SendEmailRequest.builder()
                .source(from)
                .destination(Destination.builder().toAddresses(to).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder().text(Content.builder().data(body).build()).build())
                        .build())
                .build();
        SendEmailResponse response = sesClient.sendEmail(request);
        return "Email sent! Message ID: " + response.messageId();
    }
}
