package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
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

    private final BundleAllPages bundleAllPages;

    private final AwsSesClient awsSesClient;

    public AsyncNotificationService(BundleAllPages bundleAllPages, AwsSesClient awsSesClient) {
        this.bundleAllPages = bundleAllPages;
        this.awsSesClient = awsSesClient;
    }

    /**
     * Notify all creditor institutions that have an active subscription or a request/offer to the
     * specified bundle
     *
     * @param pspCode    payment service provider code
     * @param idBundle   bundle identifier
     * @param bundleName bundle name
     * @param pspName    payment service provider name
     * @param bundleType bundle type
     */
    @Async
    public void notifyDeletePSPBundleAsync(
            String pspCode,
            String idBundle,
            String bundleName,
            String pspName,
            BundleType bundleType
    ) {
        Set<String> ciTaxCodes = this.bundleAllPages.getAllCITaxCodesAssociatedToABundle(idBundle, bundleType, pspCode);

        Context bodyContext = buildEmailHtmlBodyContext(bundleName, pspName);
        ciTaxCodes.parallelStream().forEach(
                ciTaxCode -> {
                    EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                            .institutionTaxCode(ciTaxCode)
                            .subject(BUNDLE_DELETE_SUBJECT)
                            .textBody(String.format(BUNDLE_DELETE_BODY, pspName, bundleName))
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

        if (pspName != null) {
            properties.put("pspName", pspName);
        }

        context.setVariables(properties);
        return context;

    }
}
