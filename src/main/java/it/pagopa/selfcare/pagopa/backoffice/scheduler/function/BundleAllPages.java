package it.pagopa.selfcare.pagopa.backoffice.scheduler.function;


import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreditorInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleOffers;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspBundleOffer;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequests;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class BundleAllPages {

    private final GecClient gecClient;

    private final Integer getAllBundlesPageLimit;

    public BundleAllPages(
            GecClient gecClient,
            @Value("${extraction.bundles.getAllBundles.pageLimit}") Integer getAllBundlesPageLimit
    ) {
        this.gecClient = gecClient;
        this.getAllBundlesPageLimit = getAllBundlesPageLimit;
    }

    @Cacheable(value = "getAllBundlesWithExpireDate")
    public Set<Bundle> getAllBundlesWithExpireDate(String expireAt) {
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        Bundles bundles = getAllExpiringBundles(expireAt, 1, 0);
        int numberOfPages = (int) Math.floor((double) bundles.getPageInfo().getTotalItems() / getAllBundlesPageLimit);

        List<CompletableFuture<Set<Bundle>>> futures = new LinkedList<>();

        // create parallel calls
        CompletableFuture<Set<Bundle>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> getAllExpiringBundles(expireAt, getAllBundlesPageLimit, page))
                    .flatMap(response -> response.getBundleList().stream())
                    .collect(Collectors.toSet());
        });
        futures.add(future);

        // join parallel calls
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(e -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
                .join();
    }

    @Cacheable(value = "getBundleSubscriptionByPSP")
    public Set<String> getBundleSubscriptionByPSP(String pspCode, String idBundle) {
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        BundleCreditorInstitutionResource bundles = this.gecClient.getBundleSubscriptionByPSP(pspCode, idBundle, null, 1, 0);
        int numberOfPages = (int) Math.floor((double) bundles.getPageInfo().getTotalItems() / getAllBundlesPageLimit);

        List<CompletableFuture<Set<String>>> futures = new LinkedList<>();

        // create parallel calls
        CompletableFuture<Set<String>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> this.gecClient.getBundleSubscriptionByPSP(pspCode, idBundle, null, getAllBundlesPageLimit, page))
                    .flatMap(response -> response.getCiBundleDetails().stream())
                    .map(CiBundleDetails::getCiTaxCode)
                    .collect(Collectors.toSet());
        });
        futures.add(future);

        // join parallel calls
        return joinFutures(futures);
    }

    @Cacheable(value = "getPublicBundleSubscriptionRequestByPSP")
    public Set<String> getPublicBundleSubscriptionRequestByPSP(String pspCode, String idBundle) {
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        PublicBundleRequests bundles = this.gecClient.getPublicBundleSubscriptionRequestByPSP(pspCode, idBundle, null, 1, 0);
        int numberOfPages = (int) Math.floor((double) bundles.getPageInfo().getTotalItems() / getAllBundlesPageLimit);

        List<CompletableFuture<Set<String>>> futures = new LinkedList<>();

        // create parallel calls
        CompletableFuture<Set<String>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> this.gecClient.getPublicBundleSubscriptionRequestByPSP(pspCode, idBundle, null, getAllBundlesPageLimit, page))
                    .flatMap(response -> response.getRequestsList().stream())
                    .map(PublicBundleRequest::getCiFiscalCode)
                    .collect(Collectors.toSet());
        });
        futures.add(future);

        // join parallel calls
        return joinFutures(futures);
    }

    @Cacheable(value = "getPrivateBundleOffersByPSP")
    public Set<String> getPrivateBundleOffersByPSP(String pspCode, String idBundle) {
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        BundleOffers bundles = this.gecClient.getPrivateBundleOffersByPSP(pspCode, idBundle, null, 1, 0);
        int numberOfPages = (int) Math.floor((double) bundles.getPageInfo().getTotalItems() / getAllBundlesPageLimit);

        List<CompletableFuture<Set<String>>> futures = new LinkedList<>();

        // create parallel calls
        CompletableFuture<Set<String>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> this.gecClient.getPrivateBundleOffersByPSP(pspCode, idBundle, null, getAllBundlesPageLimit, page))
                    .flatMap(response -> response.getOffers().stream())
                    .map(PspBundleOffer::getCiFiscalCode)
                    .collect(Collectors.toSet());
        });
        futures.add(future);

        // join parallel calls
        return joinFutures(futures);
    }

    @Cacheable(value = "getAllPSPBundles")
    public Set<Bundle> getAllPSPBundles(String pspCode, List<BundleType> bundleTypeList) {
        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
        Bundles bundles = getAllPSPBundles(pspCode, bundleTypeList, 1, 0);
        int numberOfPages = (int) Math.floor((double) bundles.getPageInfo().getTotalItems() / getAllBundlesPageLimit);

        List<CompletableFuture<Set<Bundle>>> futures = new LinkedList<>();

        // create parallel calls
        CompletableFuture<Set<Bundle>> future = CompletableFuture.supplyAsync(() -> {
            if (mdcContextMap != null) {
                MDC.setContextMap(mdcContextMap);
            }
            return IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(page -> getAllPSPBundles(pspCode, bundleTypeList, getAllBundlesPageLimit, page))
                    .flatMap(response -> response.getBundleList().stream())
                    .collect(Collectors.toSet());
        });
        futures.add(future);

        // join parallel calls
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(e -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
                .join();
    }

    /**
     * Retrieve a list of creditor institution tax codes that have an active subscription or a request/offer to the
     * specified bundle
     *
     * @param idBundle   bundle identifier
     * @param bundleType bundle type
     * @param pspCode    payment service provider code
     * @return the set of creditor institution tax codes
     */
    public Set<String> getAllCITaxCodesAssociatedToABundle(String idBundle, BundleType bundleType, String pspCode) {
        Set<String> bundleSubscriptions = getBundleSubscriptionByPSP(pspCode, idBundle);

        if (BundleType.PUBLIC.equals(bundleType)) {
            Set<String> requests = getPublicBundleSubscriptionRequestByPSP(pspCode, idBundle);
            bundleSubscriptions.addAll(requests);
        }
        if (BundleType.PRIVATE.equals(bundleType)) {
            Set<String> offers = getPrivateBundleOffersByPSP(pspCode, idBundle);
            bundleSubscriptions.addAll(offers);
        }
        return bundleSubscriptions;
    }

    private Bundles getAllExpiringBundles(String expireAt, Integer getAllBundlesWithExpireDatePageLimit, int page) {
        return this.gecClient.getBundles(
                List.of(BundleType.GLOBAL, BundleType.PUBLIC, BundleType.PRIVATE),
                null,
                null,
                expireAt,
                getAllBundlesWithExpireDatePageLimit,
                page);
    }

    private Bundles getAllPSPBundles(String pspCode, List<BundleType> bundleTypeList, Integer getAllBundlesWithExpireDatePageLimit, int page) {
        return this.gecClient.getBundlesByPSP(
                pspCode,
                bundleTypeList,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                getAllBundlesWithExpireDatePageLimit,
                page);
    }

    private Set<String> joinFutures(List<CompletableFuture<Set<String>>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(e -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
                .join();
    }
}
