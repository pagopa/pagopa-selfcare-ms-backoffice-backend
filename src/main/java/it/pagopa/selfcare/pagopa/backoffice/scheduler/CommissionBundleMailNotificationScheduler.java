package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCError;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForEndExecution;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForStartExecution;
import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.BUNDLE_EXPIRE_BODY;
import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.BUNDLE_EXPIRE_SUBJECT;

/**
 * Contains the scheduled function to be used for bundles expiring email notification. It retrieves all expiring bundles
 * and notify all the associated CI and PSP. In addition, it creates a SMO JIRA ticket for each bundle.
 */
@Component
@Slf4j
public class CommissionBundleMailNotificationScheduler {

    private final String environment;

    private static final String VALID_FROM_DATE_FORMAT = "yyyy-MM-dd";

    private final BundleAllPages bundleAllPages;

    private final ApiConfigClient apiConfigClient;

    private final AwsSesClient awsSesClient;

    private final JiraServiceManagerClient jsmClient;

    public CommissionBundleMailNotificationScheduler(
            @Value("${info.properties.environment}") String environment,
            BundleAllPages bundleAllPages,
            ApiConfigClient apiConfigClient,
            AwsSesClient awsSesClient,
            JiraServiceManagerClient jsmClient
    ) {
        this.environment = environment;
        this.bundleAllPages = bundleAllPages;
        this.apiConfigClient = apiConfigClient;
        this.awsSesClient = awsSesClient;
        this.jsmClient = jsmClient;
    }

    /**
     * Method containing the scheduled function
     */
    @Scheduled(cron = "${cron.job.schedule.expression.commission-bundle-mail-notification}")
    @SchedulerLock(name = "commissionBundleMailNotification", lockAtMostFor = "180m", lockAtLeastFor = "15m")
    @Async
    @Transactional
    public void mailNotification() {
        updateMDCForStartExecution("commissionBundleMailNotification", "");
        log.info("[Mail-Notification] process starting");
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            LocalDate in7Days = LocalDate.now().plusDays(7);

            notify(in7Days);
            notify(tomorrow);

            updateMDCForEndExecution();
            log.info("[Mail-Notification] process completed");
        } catch (Exception e) {
            updateMDCError(e, "Notify expired bundles");
            log.error("[Mail-Notification] an error occurred during the mail notification process", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private void notify(LocalDate date) {
        String expireAt = date.format(DateTimeFormatter.ofPattern(VALID_FROM_DATE_FORMAT));
        Set<Bundle> expiringBundles = this.bundleAllPages.getAllBundlesWithExpireDate(expireAt);
        log.info("[Mail-Notification] {} expiring bundle in {} days retrieved",
                expiringBundles.size(),
                expireAt
        );

        log.info("[Mail-Notification] Mail notification starting");
        expiringBundles.parallelStream()
                .forEach(bundle -> {
                    String pspTaxCode = getPspTaxCode(bundle);
                    // notify CI
                    this.bundleAllPages
                            .getAllCITaxCodesAssociatedToABundle(bundle.getId(), bundle.getType(), bundle.getIdPsp()).parallelStream()
                            .forEach(ciTaxCode -> sendMail(expireAt, bundle, ciTaxCode, pspTaxCode));

                    // notify PSP
                    if (pspTaxCode != null) {
                        sendMail(expireAt, bundle, pspTaxCode, pspTaxCode);
                    }

                    this.jsmClient.createTicket(
                            BUNDLE_EXPIRE_SUBJECT + " - " + bundle.getName()+ " - " + bundle.getPspBusinessName(),
                            getBundleExpireBody(bundle.getName(), bundle.getPspBusinessName(),getFormattedPspTaxCode(pspTaxCode) , expireAt)
                    );

                });
        log.info("[Mail-Notification] Mail notification completed for expiring bundle in {} days",
                expireAt);
    }

    private void sendMail(String expireAt, Bundle bundle, String notifyTaxCode, String pspTaxCode) {
        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(notifyTaxCode)
                .subject(BUNDLE_EXPIRE_SUBJECT)
                .textBody(getBundleExpireBody(bundle.getName(), bundle.getPspBusinessName(), pspTaxCode, expireAt))
                .htmlBodyFileName("expiringBundleEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundle.getName(), bundle.getPspBusinessName(), getFormattedPspTaxCode(pspTaxCode), expireAt))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();
        this.awsSesClient.sendEmail(messageDetail);
    }

    private String getPspTaxCode(Bundle bundle) {
        try {
            return this.apiConfigClient.getPSPDetails(bundle.getIdPsp()).getTaxCode();
        } catch (Exception e) {
            log.warn("[Mail-Notification] a error occurred while retrieving tax code for PSP with code {}, PSP expire notification for bundle with id {} skipped",
                    bundle.getIdPsp(), bundle.getId(), e);
            return null;
        }
    }

    private Context buildEmailHtmlBodyContext(
            String bundleName,
            String pspBusinessName,
            String pspTaxCode,
            String expireAt
    ) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("bundleName", bundleName);
        properties.put("pspName", pspBusinessName);
        properties.put("pspTaxCode", pspTaxCode);
        properties.put("expireAt", expireAt);
        properties.put("environment", getEnvParam());

        context.setVariables(properties);
        return context;
    }

    private String getEnvParam() {
        if (this.environment.equals("PROD")) {
            return "";
        }
        return String.format(".%s", this.environment.toLowerCase());
    }

    private String getBundleExpireBody(
            String bundleName,
            String pspBusinessName,
            String pspTaxCode,
            String expireAt
    ) {
        return String.format(BUNDLE_EXPIRE_BODY, bundleName, pspBusinessName, pspTaxCode, expireAt, getEnvParam());
    }

    private String getFormattedPspTaxCode(String pspTaxCode) {
        return pspTaxCode == null || pspTaxCode.isEmpty()  ? "n/a" : pspTaxCode;
    }

}
