package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreditorInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleOffers;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspBundleOffer;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequests;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCError;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForEndExecution;
import static it.pagopa.selfcare.pagopa.backoffice.scheduler.utils.SchedulerUtils.updateMDCForStartExecution;

/**
 * Contains the scheduled function to be used for bundles expiring email notification. It retrieves all expiring bundles
 * and notify all the associated CI and PSP
 */
@Component
@Slf4j
public class CommissionBundleMailNotification {

    private static final String BUNDLE_EXPIRE_SUBJECT = "Notifica scadenza pacchetto";
    private static final String BUNDLE_EXPIRE_PSP_BODY = "Ciao, %n%n%n il pacchetto %s scadrà il %s.%n%n%n Puoi gestire i tuoi pacchetti qui https://selfcare.platform.pagopa.it/ui/comm-bundles ( https://selfcare.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";
    private static final String BUNDLE_EXPIRE_CI_BODY = "Ciao, %n%n%n il pacchetto %s scadrà il %s.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nPagamenti pagoPa";

    private static final String VALID_FROM_DATE_FORMAT = "yyyy-MM-dd";

    private final GecClient gecClient;

    private final ApiConfigClient apiConfigClient;

    private final AwsSesClient awsSesClient;

    public CommissionBundleMailNotification(
            GecClient gecClient, ApiConfigClient apiConfigClient,
            AwsSesClient awsSesClient
    ) {
        this.gecClient = gecClient;
        this.apiConfigClient = apiConfigClient;
        this.awsSesClient = awsSesClient;
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
            String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(VALID_FROM_DATE_FORMAT));
            String in7Days = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern(VALID_FROM_DATE_FORMAT));

            notify(in7Days);
            notify(tomorrow);

            updateMDCForEndExecution();
            log.info("[Mail-Notification] process completed");
        } catch (Exception e) {
            updateMDCError(e, "Extract Taxonomies");
            log.error("[Mail-Notification] an error occurred during the mail notification process", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private void notify(String expireAt) {
        Bundles expiringBundles = getAllBundlesWithExpireDate(expireAt);
        log.info("[Mail-Notification] {} expiring bundle in {} days retrieved",
                expiringBundles.getPageInfo().getTotalItems(),
                expireAt
        );

        log.info("[Mail-Notification] CI mail notification starting");
        // notify CI
        expiringBundles.getBundleList().parallelStream()
                .forEach(bundle -> getAllCITaxCodesAssociatedToABundle(bundle.getId(), bundle.getType(), bundle.getIdPsp()).parallelStream()
                        .forEach(ciTaxCode -> {
                            EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                                    .institutionTaxCode(ciTaxCode)
                                    .subject(BUNDLE_EXPIRE_SUBJECT)
                                    .textBody(String.format(BUNDLE_EXPIRE_CI_BODY, bundle.getName(), expireAt))
                                    .htmlBodyFileName("createBundleSubscriptionOfferEmail.html")
                                    .htmlBodyContext(buildEmailHtmlBodyContext(bundle.getName()))
                                    .destinationUserType(SelfcareProductUser.ADMIN)
                                    .build();
                            this.awsSesClient.sendEmail(messageDetail);
                        }));

        log.info("[Mail-Notification] CI mail notification completed, PSP notification starting");
        Map<String, List<String>> mailMap = expiringBundles.getBundleList().parallelStream()
                .map(bundle -> BundleInfo.builder()
                        .pspTaxCode("") // TODO how to retrieve pspTaxCode?
                        .bundleName(bundle.getName())
                        .build())
                .collect(Collectors.groupingBy(BundleInfo::getPspTaxCode,
                        Collectors.mapping(BundleInfo::getBundleName, Collectors.toList())));

        // notify PSP
        mailMap.keySet().parallelStream().forEach(
                pspTaxCode -> {
                    List<String> bundleNames = mailMap.get(pspTaxCode);
                    EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                            .institutionTaxCode(pspTaxCode)
                            .subject(BUNDLE_EXPIRE_SUBJECT)
                            .textBody(String.format(BUNDLE_EXPIRE_PSP_BODY, bundleNames, expireAt))
                            .htmlBodyFileName("deleteBundleEmail.html")
                            .htmlBodyContext(buildEmailHtmlBodyContext(bundleNames))
                            .destinationUserType(SelfcareProductUser.ADMIN)
                            .build();
                    this.awsSesClient.sendEmail(messageDetail);
                }
        );
        log.info("[Mail-Notification] PSP mail notification completed, Email notification completed for expiring bundle in {} days",
                expireAt);
    }

    private Bundles getAllBundlesWithExpireDate(String expireAt) {
        return this.gecClient.getBundles(
                List.of(BundleType.GLOBAL, BundleType.PUBLIC, BundleType.PRIVATE),
                null,
                null,
                expireAt,
                1000,
                0);
    }

    private Context buildEmailHtmlBodyContext(String bundleName) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("bundleName", bundleName);

        context.setVariables(properties);
        return context;
    }

    private Context buildEmailHtmlBodyContext(List<String> bundleNames) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("bundleNames", bundleNames);

        context.setVariables(properties);
        return context;
    }

    private List<String> getAllCITaxCodesAssociatedToABundle(String idBundle, BundleType bundleType, String pspCode) {
        BundleCreditorInstitutionResource bundleSubscriptions = this.gecClient
                .getBundleSubscriptionByPSP(pspCode, idBundle, null, 1000, 0);
        List<String> ciTaxCodes = new ArrayList<>(
                bundleSubscriptions.getCiBundleDetails().parallelStream()
                        .map(CiBundleDetails::getCiTaxCode)
                        .toList()
        );

        if (BundleType.PUBLIC.equals(bundleType)) {
            PublicBundleRequests requests = this.gecClient
                    .getPublicBundleSubscriptionRequestByPSP(pspCode, null, idBundle, 1000, 0);
            ciTaxCodes.addAll(
                    requests.getRequestsList().parallelStream()
                            .map(PublicBundleRequest::getCiFiscalCode)
                            .toList()
            );
        }
        if (BundleType.PRIVATE.equals(bundleType)) {
            BundleOffers offers = this.gecClient
                    .getPrivateBundleOffersByPSP(pspCode, null, idBundle, 1000, 0);
            ciTaxCodes.addAll(
                    offers.getOffers().parallelStream()
                            .map(PspBundleOffer::getCiFiscalCode)
                            .toList()
            );
        }
        return ciTaxCodes;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BundleInfo {
        private String pspTaxCode;
        private String bundleName;
    }

}
