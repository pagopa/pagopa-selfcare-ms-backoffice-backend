package it.pagopa.selfcare.pagopa.backoffice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@Slf4j
public class AwsSesClient {

    private final SesClient sesClient;

    private final String from;

    private final SpringTemplateEngine templateEngine;

    @Autowired
    public AwsSesClient(
            SesClient sesClient,
            @Value("${aws.ses.user}") String from,
            SpringTemplateEngine templateEngine) {
        this.sesClient = sesClient;
        this.from = from;
        this.templateEngine = templateEngine;
    }

    public String sendEmail(String subject, String textBody, String htmlBodyFileName, Context htmlContext, String... to) {
        String result = null;
        try {
            result = sendEmailAux(subject, textBody, htmlBodyFileName, htmlContext, to);
        } catch (Exception e) {
            log.error("An error occurred while sending email with subject {}", subject, e);
            result = "sendEmail error to = " + Arrays.toString(to) + ", subject = " + subject;
        }
        return result;
    }


    private String sendEmailAux(String subject, String textBody, String htmlBodyFileName, Context htmlContext, String... to) {
        String html = templateEngine.process(htmlBodyFileName, htmlContext);

        SendEmailRequest request = SendEmailRequest.builder()
                .source(from)
                .destination(d -> d.toAddresses(to))
                .message(m -> m
                        .subject(c -> c.data(subject))
                        .body(b -> b
                                .html(c -> c.data(html).charset(StandardCharsets.UTF_8.name()))
                                .text(c -> c.data(textBody))
                                .build())
                        .build())
                .build();
        SendEmailResponse response = sesClient.sendEmail(request);
        return "Email sent! Message ID: " + response.messageId();
    }
}
