package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AwsSesClient {

    private final SesClient sesClient;

    private final String from;

    private final SpringTemplateEngine templateEngine;

    private final ExternalApiClient externalApiClient;

    private final String environment;

    private final LoggingUtils loggingUtils;

    @Autowired
    public AwsSesClient(
            SesClient sesClient,
            @Value("${aws.ses.user}") String from,
            SpringTemplateEngine templateEngine,
            ExternalApiClient externalApiClient,
            @Value("${info.properties.environment}") String environment,
            LoggingUtils loggingUtils
    ) {
        this.sesClient = sesClient;
        this.from = from;
        this.templateEngine = templateEngine;
        this.externalApiClient = externalApiClient;
        this.environment = environment;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Build and send an email with the provided info.
     * <p> The email is sent only if the environment is prod.
     * <p> Retrieve the destination of the mail based on the specified {@link SelfcareProductUser}:
     * <ul>
     *     <li> if {@link SelfcareProductUser#ADMIN} notify all payment contacts
     *     <li> if {@link SelfcareProductUser#OPERATOR} notify all technical contacts
     * </ul>
     *
     * @param email contains all the necessary info to build and send the email
     */
    public void sendEmail(EmailMessageDetail email) {
        String taxCode = email.getInstitutionTaxCode();
        if (!this.environment.equals("prod") || taxCode == null) {
            log.warn("Skip send email process");
            return;
        }

        String[] toAddressList = getToAddressList(taxCode, email.getDestinationUserType());
        if (toAddressList.length == 0) {
            log.warn("No email to be notified found for the institution with tax code {}, skip send email process",
                    loggingUtils.checkLogParam(taxCode));
            return;
        }

        try {
            SendEmailRequest request = buildEmailRequest(email, toAddressList);
            SendEmailResponse response = this.sesClient.sendEmail(request);
            log.debug("Email sent! Message ID: {}", response.messageId());
        } catch (Exception e) {
            log.error("An error occurred while sending email with subject {} to institution with tax code {}",
                    email.getSubject(), loggingUtils.checkLogParam(email.getInstitutionTaxCode()), e);
        }
    }

    private SendEmailRequest buildEmailRequest(EmailMessageDetail email, String[] toAddressList) {
        String html = this.templateEngine.process(email.getHtmlBodyFileName(), email.getHtmlBodyContext());

        return SendEmailRequest.builder()
                .source(this.from)
                .destination(d -> d.toAddresses(toAddressList))
                .message(m -> m
                        .subject(c -> c.data(email.getSubject()))
                        .body(b -> b
                                .html(c -> c.data(html).charset(StandardCharsets.UTF_8.name()))
                                .text(c -> c.data(email.getTextBody()))
                                .build())
                        .build())
                .build();
    }

    private String[] getToAddressList(String taxCode, SelfcareProductUser destinationUserType) {
        Optional<Institution> optionalInstitution = this.externalApiClient.getInstitutionsFiltered(taxCode)
                .getInstitutions().stream()
                .findFirst();

        if (optionalInstitution.isEmpty()) {
            log.debug("Unable to find the institution with tax code {}, skip send email process", loggingUtils.checkLogParam(taxCode));
            return new String[0];
        }
        Institution institution = optionalInstitution.get();
        List<InstitutionProductUsers> institutionUserList =
                this.externalApiClient.getInstitutionProductUsers(
                        institution.getId(),
                        null,
                        null,
                        Collections.singletonList(destinationUserType.getProductUser())
                );

        return institutionUserList.stream()
                .map(InstitutionProductUsers::getEmail)
                .toList()
                .toArray(new String[0]);
    }
}
