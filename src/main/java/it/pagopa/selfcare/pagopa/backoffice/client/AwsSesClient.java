package it.pagopa.selfcare.pagopa.backoffice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

import java.util.Arrays;

@Service
public class AwsSesClient {

    @Autowired
    private SesClient sesClient;

    @Value("${aws.ses.user}")
    private String from;


    public String sendEmail(String subject, String body, String... to) {
        String result = null;
        try {
            result = sendEmailAux(subject, body, to);
        } catch (Exception e) {
            result = "sendEmail error to = " + Arrays.toString(to) + ", subject = " + subject;
        }
        return result;
    }


    private String sendEmailAux(String subject, String body, String... to) {
        SendEmailRequest request = SendEmailRequest.builder()
                .source(from)
                .destination(d -> d.toAddresses(to).build())
                .message(m -> m.subject(c -> c.data(subject).build())
                        .body(b -> b.text(c -> c.data(body).build()).build())
                        .build())
                .build();
        SendEmailResponse response = sesClient.sendEmail(request);
        return "Email sent! Message ID: " + response.messageId();
    }

}
