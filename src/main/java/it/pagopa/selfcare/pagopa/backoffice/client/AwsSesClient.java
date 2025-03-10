package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private final Boolean enableSendEmail;

    private final String testEmailAddress;

    private final String pagopaOperatorEmailAddress;

    @Autowired
    public AwsSesClient(
            SesClient sesClient,
            @Value("${aws.ses.user}") String from,
            SpringTemplateEngine templateEngine,
            ExternalApiClient externalApiClient,
            @Value("${info.properties.environment}") String environment,
            @Value("${institution.subscription.test-email}") String testEmailAddress,
            @Value("${institution.subscription.pagopa-operator-email}") String pagopaOperatorEmailAddress,
            @Value("${institution.subscription.enable-send-email}") Boolean enableSendEmail) {
        this.sesClient = sesClient;
        this.from = from;
        this.templateEngine = templateEngine;
        this.externalApiClient = externalApiClient;
        this.environment = environment;
        this.testEmailAddress = testEmailAddress;
        this.pagopaOperatorEmailAddress = pagopaOperatorEmailAddress;
        this.enableSendEmail = enableSendEmail;
    }

    /**
     * Build and send an email with the provided info.
     * <p> The email is sent only if the environment is prod.
     * <p> Retrieve the destination of the mail based on the specified {@link SelfcareProductUser}:
     * <ul>
     *     <li> if {@link SelfcareProductUser#ADMIN} notify all payment contacts
     *     <li> if {@link SelfcareProductUser#OPERATOR} notify all technical contacts
     * </ul>
     * Optionally sends a blind carbon copy of the email to the pagopa operator
     *
     * @param email contains all the necessary info to build and send the email
     * @param sendEmailToPagopaOperator if true sends a copy of the email to the pagopa operator
     */
    public void sendEmail(EmailMessageDetail email, boolean sendEmailToPagopaOperator) {
        String taxCode = email.getInstitutionTaxCode();
        if (hasNotRequiredData(taxCode)) {
            log.warn("Skip send email process");
            return;
        }

        String[] toAddressList = getToAddressList(taxCode, email.getDestinationUserType());
        if (toAddressList.length == 0) {
            log.warn("No email to be notified found for the institution with tax code {}, skip send email process",
                    Utility.sanitizeLogParam(taxCode));
            return;
        }

        try {
            SendEmailRequest request = buildEmailRequest(email, toAddressList, sendEmailToPagopaOperator);
            SendEmailResponse response = this.sesClient.sendEmail(request);
            log.debug("Email sent! Message ID: {}", response.messageId());
        } catch (Exception e) {
            log.error("An error occurred while sending email with subject {} to institution with tax code {}",
                    email.getSubject(), Utility.sanitizeLogParam(email.getInstitutionTaxCode()), e);
        }
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
        sendEmail(email, false);
    }

    private SendEmailRequest buildEmailRequest(EmailMessageDetail email, String[] toAddressList, boolean sendEmailToPagopaOperator) {
        String html = this.templateEngine.process(email.getHtmlBodyFileName(), email.getHtmlBodyContext());
        String subject = isNotProd() ? String.format("[%s] %s", this.environment, email.getSubject()) : email.getSubject();

        return SendEmailRequest.builder()
                .source(this.from)
                .destination(d -> {
                    if (Boolean.TRUE.equals(sendEmailToPagopaOperator) && !pagopaOperatorEmailAddress.isEmpty()) {
                        d.toAddresses(toAddressList).bccAddresses(pagopaOperatorEmailAddress);
                    } else {
                        d.toAddresses(toAddressList);
                    }
                })
                .message(m -> m
                        .subject(c -> c.data(subject))
                        .body(b -> b
                                .html(c -> c.data(html).charset(StandardCharsets.UTF_8.name()))
                                .text(c -> c.data(email.getTextBody()))
                                .build())
                        .build())
                .build();
    }

    private String[] getToAddressList(String taxCode, SelfcareProductUser destinationUserType) {
        if (Boolean.FALSE.equals(this.enableSendEmail)) {
            return new String[]{testEmailAddress};
        }
        Optional<Institution> optionalInstitution = this.externalApiClient.getInstitutionsFiltered(taxCode)
                .getInstitutions().stream()
                .findFirst();

        if (optionalInstitution.isEmpty()) {
            log.debug("Unable to find the institution with tax code {}, skip send email process", Utility.sanitizeLogParam(taxCode));
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

    private boolean isNotProd() {
        return !this.environment.equals("PROD");
    }

    private boolean hasNotRequiredData(String taxCode) {
        return Boolean.FALSE.equals(this.enableSendEmail) ? StringUtils.isBlank(testEmailAddress) : taxCode == null;
    }
}
