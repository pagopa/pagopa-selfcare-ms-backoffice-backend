package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.BUNDLE_DELETE_BODY;
import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.BUNDLE_DELETE_SUBJECT;

@Service
public class AsyncNotificationService {

    private final String environment;
    private final AwsSesClient awsSesClient;

    public AsyncNotificationService(
            @Value("${info.properties.environment}") String environment,
            AwsSesClient awsSesClient
    ) {
        this.environment = environment;
        this.awsSesClient = awsSesClient;
    }

    /**
     * Notify all creditor institutions that have an active subscription or a request/offer to the
     * specified bundle
     *
     * @param ciTaxCodes  set of tax codes to be notified
     * @param bundleName bundle name
     * @param pspName    payment service provider name
     */
    @Async
    public void notifyDeletePSPBundleAsync(
            Set<String> ciTaxCodes,
            String bundleName,
            String pspName
    ) {
        Context bodyContext = buildEmailHtmlBodyContext(bundleName, pspName);
        ciTaxCodes.parallelStream().forEach(
                ciTaxCode -> {
                    EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                            .institutionTaxCode(ciTaxCode)
                            .subject(BUNDLE_DELETE_SUBJECT)
                            .textBody(String.format(BUNDLE_DELETE_BODY, pspName, bundleName, getEnvParam()))
                            .htmlBodyFileName("deleteBundleEmail.html")
                            .htmlBodyContext(bodyContext)
                            .destinationUserType(SelfcareProductUser.ADMIN)
                            .build();
                    this.awsSesClient.sendEmail(messageDetail);
                }
        );
    }

    private Context buildEmailHtmlBodyContext(String bundleName, String pspName) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("bundleName", bundleName);
        properties.put("environment", getEnvParam());

        if (pspName != null) {
            properties.put("pspName", pspName);
        }

        context.setVariables(properties);
        return context;
    }

    private String getEnvParam() {
        if (this.environment.equals("PROD")) {
            return "";
        }
        return String.format(".%s", this.environment.toLowerCase());
    }
}
