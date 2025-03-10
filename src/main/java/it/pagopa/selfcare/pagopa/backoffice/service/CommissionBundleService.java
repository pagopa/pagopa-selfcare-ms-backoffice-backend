package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.*;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.BundleAllPages;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static it.pagopa.selfcare.pagopa.backoffice.util.MailTextConstants.*;
import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;

@Slf4j
@Service
public class CommissionBundleService {

    private static final String VALID_FROM_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SUBJECT_NEW_BUNDLE_GEC = "Creazione Nuovo Pacchetto GEC %s";
    public static final String DETAIL_NEW_BUNDLE_GEC = "E' stato creato un nuovo pacchetto GEC '%s' da parte di %s (CF: %s) che si attiverà a partire dal %s. Si prega di prenderne visione per verificare se il PSP ha configurato correttamente il pacchetto. \nLink: https://selfcare%s.platform.pagopa.it/ui/comm-bundles/detail/%s";

    private final String environment;

    private final GecClient gecClient;

    private final ModelMapper modelMapper;

    private final TaxonomyService taxonomyService;

    private final LegacyPspCodeUtil legacyPspCodeUtil;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final AwsSesClient awsSesClient;

    private final AsyncNotificationService asyncNotificationService;

    private final JiraServiceManagerClient jiraServiceManagerClient;

    private final ExportService exportService;

    private final BundleAllPages bundleAllPages;

    @Autowired
    public CommissionBundleService(
            @Value("${info.properties.environment}") String environment,
            GecClient gecClient,
            ModelMapper modelMapper,
            TaxonomyService taxonomyService,
            LegacyPspCodeUtil legacyPspCodeUtil,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            AwsSesClient awsSesClient,
            AsyncNotificationService asyncNotificationService,
            JiraServiceManagerClient jiraServiceManagerClient,
            ExportService exportService,
            BundleAllPages bundleAllPages
    ) {
        this.environment = environment;
        this.gecClient = gecClient;
        this.modelMapper = modelMapper;
        this.taxonomyService = taxonomyService;
        this.legacyPspCodeUtil = legacyPspCodeUtil;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.awsSesClient = awsSesClient;
        this.asyncNotificationService = asyncNotificationService;
        this.jiraServiceManagerClient = jiraServiceManagerClient;
        this.exportService = exportService;
        this.bundleAllPages = bundleAllPages;
    }

    public BundlePaymentTypes getBundlesPaymentTypes(Integer limit, Integer page) {
        BundlePaymentTypesDTO dto = gecClient.getPaymenttypes(limit, page);
        return modelMapper.map(dto, BundlePaymentTypes.class);
    }

    public Touchpoints getTouchpoints(Integer limit, Integer page) {
        TouchpointsDTO dto = gecClient.getTouchpoints(limit, page);
        return modelMapper.map(dto, Touchpoints.class);
    }

    /**
     * Retrieve bundles' list by PSP tax code
     * @param pspTaxCode PSP's tax code
     * @param name bundle name
     * @param bundleType list of bundle types
     * @param maxPaymentAmountOrder direction to order the list based on the bundle's maxPaymentAmount
     * @param paymentAmountMinRange filters bundles with paymentAmount more than paymentAmountMinRange
     * @param paymentAmountMaxRange filters bundles with paymentAmount less than paymentAmountMaxRange
     * @param validBefore filters bundles with validityDateFrom before the value of validBefore
     * @param validAfter filters bundles with validityDateFrom after the value of validAfter
     * @param expireBefore filters bundles with validityDateTo before the value of expireBefore
     * @param expireAfter filters bundles with validityDateTo after the value of expireAfter
     * @param page page's number for pagination
     * @param limit maximum number of elements for page
     * @return list of bundles ordered and filtered
     */
    public PSPBundlesResource getBundlesByPSP(
            String pspTaxCode,
            List<BundleType> bundleType,
            String name,
            Sort.Direction maxPaymentAmountOrder,
            Long paymentAmountMinRange,
            Long paymentAmountMaxRange,
            LocalDate validBefore,
            LocalDate validAfter,
            LocalDate expireBefore,
            LocalDate expireAfter,
            Integer limit,
            Integer page
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        Bundles bundles = this.gecClient.getBundlesByPSP(pspCode, bundleType, name, maxPaymentAmountOrder, paymentAmountMinRange, paymentAmountMaxRange, validBefore, validAfter, expireBefore, expireAfter, limit, page);
        List<PSPBundleResource> bundlesResource = new ArrayList<>();
        if (bundles.getBundleList() != null) {
            bundlesResource = getPSPBundlesResource(bundles);
        }
        return PSPBundlesResource.builder().bundles(bundlesResource).pageInfo(bundles.getPageInfo()).build();

    }

    public BundleCreateResponse createPSPBundle(String pspTaxCode, BundleRequest bundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        var result = this.gecClient.createPSPBundle(pspCode, bundle);
        String url = !"UAT".equals(environment) ? "" : ".uat";
        jiraServiceManagerClient.createTicket(
                String.format(SUBJECT_NEW_BUNDLE_GEC, bundle.getPspBusinessName()),
                String.format(DETAIL_NEW_BUNDLE_GEC,
                        bundle.getName(), bundle.getPspBusinessName(), pspTaxCode, deNull(bundle.getValidityDateFrom()), url, result.getIdBundle())
        );
        return result;
    }

    public PSPBundleResource getBundleDetailByPSP(String pspTaxCode, String idBundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        Bundle bundle = this.gecClient.getBundleDetailByPSP(pspCode, idBundle);
        PSPBundleResource bundleResource = this.modelMapper.map(bundle, PSPBundleResource.class);
        bundleResource.setBundleTaxonomies(getBundleTaxonomies(bundle.getTransferCategoryList(), PSPBundleTaxonomy.class));
        return bundleResource;
    }

    public void updatePSPBundle(String pspTaxCode, String idBundle, BundleRequest bundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        gecClient.updatePSPBundle(pspCode, idBundle, bundle);
    }

    /**
     * Delete a bundle of a PSP and asynchronously notify via mail all creditor institutions that are subscribed to the bundle or have
     * active request/offer
     *
     * @param pspTaxCode tax code of the PSP that own the bundle
     * @param idBundle   bundle identifier
     * @param bundleName bundle name
     * @param bundleType bundle type
     */
    public void deletePSPBundle(
            String pspTaxCode,
            String idBundle,
            String bundleName,
            String pspName,
            BundleType bundleType
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        Set<String> ciTaxCodes = this.bundleAllPages.getAllCITaxCodesAssociatedToABundle(idBundle, bundleType, pspCode);
        this.asyncNotificationService.notifyDeletePSPBundleAsync(ciTaxCodes, bundleName, pspName);

        this.gecClient.deletePSPBundle(pspCode, idBundle);
    }

    /**
     * Retrieve the PSP code with the psp tax code and accept the list of EC subscription requests to a public bundle
     * by invoking the {@link GecClient}
     *
     * @param pspTaxCode the tax code of the PSP that owns the public bundle
     * @param requestId  the bundle request id to be accepted
     * @param ciTaxCode  creditor institution's tax code
     * @param bundleName bundle's name
     */
    public void acceptPublicBundleSubscriptionsByPSP(
            String pspTaxCode,
            String requestId,
            String ciTaxCode,
            String bundleName
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        this.gecClient.acceptPublicBundleSubscriptionsByPSP(pspCode, requestId);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(BUNDLE_ACCEPT_REQUEST_SUBJECT)
                .textBody(String.format(BUNDLE_ACCEPT_REQUEST_BODY, bundleName, getEnvParam()))
                .htmlBodyFileName("acceptBundleSubscriptionRequestEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
    }

    /**
     * Retrieve creditor institution paged bundle list of the specified type {@link CIBundleStatus}.
     * The result contains an expanded version of the bundle, using the taxonomy detail extracted
     * from the repository instance
     *
     * @param bundleType         the requested type of bundles
     * @param subscriptionStatus the status of the public/private bundle subscription, required in case of {@link BundleType#PRIVATE} otherwise is optional
     * @param ciTaxCode          creditor institution's tax code, required in case of {@link BundleType#PUBLIC} otherwise is optional and used to filter the results
     * @param limit              page limit parameter
     * @param page               page number parameter
     * @return paged list of bundle resources, expanded with taxonomy data
     */
    public CIBundlesResource getCIBundles(
            BundleType bundleType,
            BundleSubscriptionStatus subscriptionStatus,
            String ciTaxCode,
            String bundleName,
            Integer limit,
            Integer page
    ) {
        List<CIBundleResource> bundlesResource = new ArrayList<>();
        PageInfo pageInfo = new PageInfo();

        List<BundleType> bundleTypes = Collections.singletonList(bundleType);
        if (bundleType.equals(BundleType.GLOBAL)) {
            Bundles bundles = this.gecClient.getBundles(bundleTypes, bundleName, null, null, limit, page);
            pageInfo = bundles.getPageInfo();
            bundlesResource = getCIBundlesResource(bundles);
        } else if (bundleType.equals(BundleType.PUBLIC)) {
            if (ciTaxCode == null) {
                throw new AppException(AppError.INVALID_GET_PUBLIC_CI_BUNDLES_REQUEST);
            }
            String validFrom = LocalDate.now().format(DateTimeFormatter.ofPattern(VALID_FROM_DATE_FORMAT));
            Bundles bundles = this.gecClient.getBundles(bundleTypes, bundleName, validFrom, null, limit, page);
            pageInfo = bundles.getPageInfo();
            bundlesResource = bundles.getBundleList().parallelStream()
                    .map(bundle -> buildCIBundle(ciTaxCode, bundle))
                    .toList();
        } else if (bundleType.equals(BundleType.PRIVATE)) {
            if (ciTaxCode == null || subscriptionStatus == null) {
                throw new AppException(AppError.INVALID_GET_PRIVATE_CI_BUNDLES_REQUEST, ciTaxCode, subscriptionStatus);
            }
            if (BundleSubscriptionStatus.ACCEPTED.equals(subscriptionStatus)) {
                CiBundles bundlesByCI = this.gecClient.getBundlesByCI(ciTaxCode, BundleType.PRIVATE.name(), bundleName, limit, page);
                pageInfo = bundlesByCI.getPageInfo();
                bundlesResource = getAcceptedCIPrivateBundleResources(bundlesByCI);

            } else if (BundleSubscriptionStatus.WAITING.equals(subscriptionStatus)) {
                BundleCIOffers bundleOffers = this.gecClient.getOffersByCI(ciTaxCode, null, bundleName, limit, page);
                pageInfo = bundleOffers.getPageInfo();
                bundlesResource = getWaitingCIPrivateBundleResources(bundleOffers);
            }
        }
        return CIBundlesResource.builder().bundles(bundlesResource).pageInfo(pageInfo).build();
    }

    /**
     * Reject a subscription requests to a public bundle
     *
     * @param pspTaxCode      the tax code of the PSP that owns the public bundle
     * @param bundleRequestId the request id to be rejected
     * @param ciTaxCode       creditor institution's tax code
     * @param bundleName      bundle's name
     */
    public void rejectPublicBundleSubscriptionByPSP(
            String pspTaxCode,
            String bundleRequestId,
            String ciTaxCode,
            String bundleName
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        this.gecClient.rejectPublicBundleSubscriptionByPSP(pspCode, bundleRequestId);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(BUNDLE_REJECT_REQUEST_SUBJECT)
                .textBody(String.format(BUNDLE_REJECT_REQUEST_BODY, bundleName))
                .htmlBodyFileName("rejectBundleSubscriptionRequestEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
    }

    /**
     * Retrieve a paginated list of creditor institution's info that are subscribed ({@link BundleSubscriptionStatus#ACCEPTED})
     * to a public/private bundle of a PSP
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param ciTaxCode  the creditor institution's tax code
     * @param limit      the size of the page
     * @param page       the page number
     * @return a paginated list of creditor institution's info
     */
    public CIBundleSubscriptionsResource getAcceptedBundleCISubscriptions(
            String idBundle,
            String pspTaxCode,
            String ciTaxCode,
            Integer limit,
            Integer page
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);

        BundleCreditorInstitutionResource acceptedSubscription = this.gecClient
                .getBundleSubscriptionByPSP(pspCode, idBundle, ciTaxCode, limit, page);

        List<CreditorInstitutionInfo> ciInfoList = getCIInfo(acceptedSubscription);
        List<CISubscriptionInfo> ciSubscriptionInfoList = buildCISubscriptionInfoList(ciInfoList, acceptedSubscription);

        return CIBundleSubscriptionsResource.builder()
                .ciSubscriptionInfoList(ciSubscriptionInfoList)
                .pageInfo(acceptedSubscription.getPageInfo())
                .build();
    }

    /**
     * Retrieve a paginated list of creditor institution's info that have requested a subscription ({@link BundleSubscriptionStatus#WAITING})
     * to a public/private bundle of a PSP
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param bundleType the status of the subscription
     * @param ciTaxCode  the creditor institution's tax code
     * @param limit      the size of the page
     * @param page       the page number
     * @return a paginated list of creditor institution's info
     */
    public CIBundleSubscriptionsResource getWaitingBundleCISubscriptions(
            String idBundle,
            String pspTaxCode,
            BundleType bundleType,
            String ciTaxCode,
            Integer limit,
            Integer page
    ) {
        if (bundleType == null || BundleType.GLOBAL.equals(bundleType)) {
            throw new AppException(bundleType == null ? AppError.BUNDLE_SUBSCRIPTION_TYPE_NULL_BAD_REQUEST : AppError.BUNDLE_SUBSCRIPTION_BAD_REQUEST);
        }

        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        PageInfo pageInfo;
        List<CreditorInstitutionInfo> ciInfoList;

        if (BundleType.PUBLIC.equals(bundleType)) {
            PublicBundleRequests subscriptionRequest = this.gecClient
                    .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, limit, page);

            ciInfoList = getCIInfo(subscriptionRequest);
            pageInfo = subscriptionRequest.getPageInfo();
        } else {
            BundleOffers offers = this.gecClient
                    .getPrivateBundleOffersByPSP(pspCode, ciTaxCode, idBundle, limit, page);

            ciInfoList = getCIInfo(offers);
            pageInfo = offers.getPageInfo();
        }

        return CIBundleSubscriptionsResource.builder()
                .ciSubscriptionInfoList(buildCISubscriptionInfoList(ciInfoList))
                .pageInfo(pageInfo)
                .build();
    }

    /**
     * Retrieve the detail of a creditor institution's subscribed to a public/private bundle, included the specified taxonomies
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param ciTaxCode  the creditor institution's tax code
     * @return the detail of a creditor institution's subscription
     */
    public CIBundleSubscriptionsDetail getAcceptedBundleCISubscriptionsDetail(
            String idBundle,
            String pspTaxCode,
            String ciTaxCode
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);

        CiBundleDetails ciBundleDetails = this.gecClient.getBundleSubscriptionDetailByPSP(pspCode, ciTaxCode, idBundle);
        List<CIBundleFee> ciBundleFeeList = getCIBundleFeeList(ciBundleDetails.getAttributes());

        return CIBundleSubscriptionsDetail.builder()
                .ciBundleFeeList(ciBundleFeeList)
                .idCIBundle(ciBundleDetails.getIdCIBundle())
                .build();
    }

    /**
     * Retrieve the detail of a creditor institution's subscription to a public/private bundle, included the specified taxonomies
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param ciTaxCode  the creditor institution's tax code
     * @param bundleType the type of the bundle
     * @return the detail of a creditor institution's subscription
     */
    public CIBundleSubscriptionsDetail getWaitingBundleCISubscriptionsDetail(
            String idBundle,
            String pspTaxCode,
            String ciTaxCode,
            BundleType bundleType
    ) {
        if (bundleType == null || BundleType.GLOBAL.equals(bundleType)) {
            throw new AppException(bundleType == null ? AppError.BUNDLE_SUBSCRIPTION_TYPE_NULL_BAD_REQUEST : AppError.BUNDLE_SUBSCRIPTION_BAD_REQUEST);
        }

        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        List<CIBundleFee> ciBundleFeeList = Collections.emptyList();
        String bundleRequestId = null;
        String bundleOfferId = null;

        if (BundleType.PUBLIC.equals(bundleType)) {
            PublicBundleRequests subscriptionRequest = this.gecClient
                    .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, 1, 0);

            if (!subscriptionRequest.getRequestsList().isEmpty()) {
                PublicBundleRequest publicBundleRequest = subscriptionRequest.getRequestsList().get(0);

                ciBundleFeeList = getCIBundleFeeList(publicBundleRequest.getCiBundleAttributes());
                bundleRequestId = publicBundleRequest.getId();
            }
        } else {
            BundleOffers offers = this.gecClient.getPrivateBundleOffersByPSP(pspCode, ciTaxCode, idBundle, 1, 0);

            if (!offers.getOffers().isEmpty()) {
                bundleOfferId = offers.getOffers().get(0).getId();
            }
        }

        return CIBundleSubscriptionsDetail.builder()
                .ciBundleFeeList(ciBundleFeeList)
                .bundleRequestId(bundleRequestId)
                .bundleOfferId(bundleOfferId)
                .build();
    }

    /**
     * Delete the creditor institution's subscription to the specified bundle
     *
     * @param ciBundleId subscription's id of a creditor institution to a bundle
     * @param ciTaxCode  creditor institution's tax code
     * @param bundleName bundle's name
     */
    public void deleteCIBundleSubscription(String ciBundleId, String ciTaxCode, String bundleName) {
        this.gecClient.deleteCIBundle(ciTaxCode, ciBundleId);

        if (bundleName != null && !bundleName.isBlank()) {
            EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                    .institutionTaxCode(ciTaxCode)
                    .subject(BUNDLE_DELETE_SUBSCRIPTION_SUBJECT)
                    .textBody(String.format(BUNDLE_DELETE_SUBSCRIPTION_BODY, bundleName))
                    .htmlBodyFileName("deleteBundleSubscriptionEmail.html")
                    .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                    .destinationUserType(SelfcareProductUser.ADMIN)
                    .build();

            this.awsSesClient.sendEmail(messageDetail);
        }
    }

    /**
     * Delete the creditor institution's subscription request to the specified bundle
     *
     * @param idBundleRequest subscription's id of a creditor institution to a bundle
     * @param ciTaxCode       creditor institution's tax code
     */
    public void deleteCIBundleRequest(String idBundleRequest, String ciTaxCode) {
        this.gecClient.deleteCIBundleRequest(ciTaxCode, idBundleRequest);
    }

    public void createCIBundleRequest(String ciTaxCode, PublicBundleRequest bundleRequest, String bundleName) {
        this.gecClient.createCIBundleRequest(ciTaxCode, bundleRequest);

        if (bundleName != null && !bundleName.isBlank()) {
            EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                    .institutionTaxCode(bundleRequest.getIdPsp())
                    .subject(BUNDLE_CREATE_REQUEST_SUBJECT)
                    .textBody(String.format(BUNDLE_CREATE_REQUEST_BODY, bundleName, getEnvParam()))
                    .htmlBodyFileName("createBundleSubscriptionRequestEmail.html")
                    .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                    .destinationUserType(SelfcareProductUser.ADMIN)
                    .build();

            this.awsSesClient.sendEmail(messageDetail);
        }
    }

    /**
     * Delete a payment service provider's private bundle offer
     *
     * @param idBundle      private bundle id
     * @param pspTaxCode    payment service provider's tax code
     * @param bundleOfferId id of the bundle offer
     * @param ciTaxCode     tax code of the creditor institution to be notified
     * @param bundleName    name of the deleted bundle offer
     */
    public void deletePrivateBundleOffer(
            String idBundle,
            String pspTaxCode,
            String bundleOfferId,
            String ciTaxCode,
            String bundleName
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        this.gecClient.deletePrivateBundleOffer(pspCode, idBundle, bundleOfferId);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(BUNDLE_DELETE_OFFER_SUBJECT)
                .textBody(String.format(BUNDLE_DELETE_OFFER_BODY, bundleName))
                .htmlBodyFileName("deleteBundleSubscriptionOfferEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
    }

    /**
     * Create the subscription offer for the specified private bundle and notify all the interested creditor institution's
     *
     * @param idBundle      the private bundle id
     * @param pspTaxCode    Payment Service Provider's tax code
     * @param bundleName    the private bundle name
     * @param ciTaxCodeList the list tax code of creditor institutions tha will receive the offer
     */
    public void createCIBundleOffers(
            String idBundle,
            String pspTaxCode,
            String bundleName,
            CiTaxCodeList ciTaxCodeList
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        this.gecClient.createPrivateBundleOffer(pspCode, idBundle, ciTaxCodeList);

        Context bodyContext = buildEmailHtmlBodyContext(bundleName);
        ciTaxCodeList.getCiTaxCodes().parallelStream()
                .forEach(ciTaxCode -> {
                    EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                            .institutionTaxCode(ciTaxCode)
                            .subject(BUNDLE_CREATE_OFFER_SUBJECT)
                            .textBody(String.format(BUNDLE_CREATE_OFFER_BODY, bundleName, getEnvParam()))
                            .htmlBodyFileName("createBundleSubscriptionOfferEmail.html")
                            .htmlBodyContext(bodyContext)
                            .destinationUserType(SelfcareProductUser.ADMIN)
                            .build();
                    this.awsSesClient.sendEmail(messageDetail);
                });
    }

    /**
     * Accept the private bundle offer with the provided id.
     * The provided tax code identifies the creditor institution that accept the offer.
     * Notify the PSP with the provided tax code.
     *
     * @param ciTaxCode          the tax code of the creditor institution
     * @param idBundleOffer      th id of the bundle offer
     * @param pspTaxCode         tax code of the PSP to be notified
     * @param bundleName         name of the offered bundle
     * @param ciBundleAttributes bundle attributes specified by the creditor institution
     * @return the id of the accepted private bundle
     */
    public CIBundleId acceptPrivateBundleOffer(
            String ciTaxCode,
            String idBundleOffer,
            String pspTaxCode,
            String bundleName,
            CIBundleAttributeResource ciBundleAttributes
    ) {
        CIBundleId ciBundleId = this.gecClient.acceptPrivateBundleOffer(ciTaxCode, idBundleOffer, ciBundleAttributes.getAttributes());

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(pspTaxCode)
                .subject(BUNDLE_ACCEPT_OFFER_SUBJECT)
                .textBody(String.format(BUNDLE_ACCEPT_OFFER_BODY, bundleName, getEnvParam()))
                .htmlBodyFileName("acceptBundleSubscriptionOfferEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
        return ciBundleId;
    }

    /**
     * Reject the private bundle offer with the provided id.
     * The provided tax code identifies the creditor institution that reject the offer.
     * Notify the PSP with the provided tax code.
     *
     * @param ciTaxCode     the tax code of the creditor institution
     * @param idBundleOffer th id of the bundle offer
     * @param pspTaxCode    tax code of the PSP to be notified
     * @param bundleName    name of the offered bundle
     */
    public void rejectPrivateBundleOffer(String ciTaxCode, String idBundleOffer, String pspTaxCode, String bundleName) {
        this.gecClient.rejectPrivateBundleOffer(ciTaxCode, idBundleOffer);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(pspTaxCode)
                .subject(BUNDLE_REJECT_OFFER_SUBJECT)
                .textBody(String.format(BUNDLE_REJECT_OFFER_BODY, bundleName))
                .htmlBodyFileName("rejectBundleSubscriptionOfferEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        this.awsSesClient.sendEmail(messageDetail);
    }

    /**
     * Export all bundles of the specified PSP and bundle types.
     * <p>
     * Retrieves all bundles with the provided filters and return the list in CSV format
     *
     * @param pspTaxCode PSP's tax code
     * @param bundleTypeList the types of bundle to be retrieved
     * @return the bundles in CSV format
     */
    public byte[] exportPSPBundleList(String pspTaxCode, List<BundleType> bundleTypeList) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);

        return this.exportService.exportPSPBundlesToCsv(pspCode, bundleTypeList);
    }

    private Context buildEmailHtmlBodyContext(String bundleName) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("bundleName", bundleName);
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

    private List<CISubscriptionInfo> buildCISubscriptionInfoList(
            List<CreditorInstitutionInfo> ciInfoList,
            BundleCreditorInstitutionResource acceptedSubscription
    ) {
        LocalDate today = LocalDate.now();

        return ciInfoList.parallelStream()
                .map(ciInfo -> {
                    CISubscriptionInfo subscriptionInfo = this.modelMapper.map(ciInfo, CISubscriptionInfo.class);
                    subscriptionInfo.setOnRemoval(isOnRemoval(acceptedSubscription, ciInfo, today));
                    return subscriptionInfo;
                })
                .toList();
    }

    private boolean isOnRemoval(
            BundleCreditorInstitutionResource acceptedSubscription,
            CreditorInstitutionInfo ciInfo,
            LocalDate today
    ) {
        LocalDate validityDateTo = acceptedSubscription.getCiBundleDetails().stream()
                .filter(s -> s.getCiTaxCode().equals(ciInfo.getCiTaxCode()))
                .findFirst()
                .orElse(new CiBundleDetails())
                .getValidityDateTo();

        return validityDateTo != null && (validityDateTo.isBefore(today) || validityDateTo.isEqual(today));
    }

    private List<CISubscriptionInfo> buildCISubscriptionInfoList(List<CreditorInstitutionInfo> ciInfoList) {
        return ciInfoList.parallelStream()
                .map(ciInfo -> this.modelMapper.map(ciInfo, CISubscriptionInfo.class))
                .toList();
    }

    private List<CIBundleFee> getCIBundleFeeList(List<CIBundleAttribute> attributes) {
        List<Taxonomy> taxonomies = getTaxonomiesByBundleAttributes(attributes);

        return attributes.parallelStream()
                .map(attribute -> buildCIBundleFee(attribute, taxonomies))
                .toList();
    }

    private List<CreditorInstitutionInfo> getCIInfo(BundleCreditorInstitutionResource acceptedSubscription) {
        List<String> taxCodeList = acceptedSubscription.getCiBundleDetails().parallelStream()
                .map(CiBundleDetails::getCiTaxCode)
                .toList();
        return this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(taxCodeList);
    }

    private List<CreditorInstitutionInfo> getCIInfo(PublicBundleRequests subscriptionRequest) {
        List<String> taxCodeList = subscriptionRequest.getRequestsList().parallelStream()
                .map(PublicBundleRequest::getCiFiscalCode)
                .toList();
        return this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(taxCodeList);
    }

    private List<CreditorInstitutionInfo> getCIInfo(BundleOffers bundleOffers) {
        List<String> taxCodeList = bundleOffers.getOffers().parallelStream()
                .map(PspBundleOffer::getCiFiscalCode)
                .toList();
        return this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(taxCodeList);
    }

    private List<Taxonomy> getTaxonomiesByBundleAttributes(List<CIBundleAttribute> ciBundleAttributes) {
        List<String> transferCategoryList = ciBundleAttributes.parallelStream()
                .map(CIBundleAttribute::getTransferCategory)
                .toList();

        return this.taxonomyService.getTaxonomiesByCodes(transferCategoryList);
    }

    private CIBundleFee buildCIBundleFee(CIBundleAttribute attribute, List<Taxonomy> taxonomies) {
        String transferCategory = attribute.getTransferCategory();
        return CIBundleFee.builder()
                .paymentAmount(attribute.getMaxPaymentAmount())
                .specificBuiltInData(transferCategory)
                .serviceType(taxonomies.stream()
                        .filter(taxonomy -> taxonomy.getSpecificBuiltInData().equals(transferCategory))
                        .findFirst()
                        .orElse(new Taxonomy())
                        .getServiceType())
                .build();
    }

    private List<PSPBundleResource> getPSPBundlesResource(Bundles bundles) {
        return bundles.getBundleList().stream().map(bundle -> {
            PSPBundleResource bundleResource = this.modelMapper.map(bundle, PSPBundleResource.class);
            List<PSPBundleTaxonomy> bundleTaxonomies = getBundleTaxonomies(bundle.getTransferCategoryList(), PSPBundleTaxonomy.class);
            bundleResource.setBundleTaxonomies(bundleTaxonomies);
            return bundleResource;
        }).toList();
    }

    private <T extends BundleTaxonomy> List<T> getBundleTaxonomies(
            List<String> transferCategoryList,
            Class<T> bundleTaxonomyClazz
    ) {
        List<Taxonomy> taxonomies = this.taxonomyService.getTaxonomiesByCodes(transferCategoryList);
        return taxonomies.parallelStream()
                .map(taxonomy -> this.modelMapper.map(taxonomy, bundleTaxonomyClazz))
                .toList();
    }

    private List<CIBundleResource> getCIBundlesResource(Bundles bundles) {
        return bundles.getBundleList().stream().map(bundle -> {
            CIBundleResource bundleResource = this.modelMapper.map(bundle, CIBundleResource.class);
            List<CIBundleFee> bundleTaxonomies = getBundleTaxonomies(bundle.getTransferCategoryList(), CIBundleFee.class);
            bundleResource.setCiBundleFeeList(bundleTaxonomies);
            return bundleResource;
        }).toList();
    }

    private CIBundleResource buildCIBundle(String ciTaxCode, Bundle bundle) {
        CIBundleResource bundleResource = this.modelMapper.map(bundle, CIBundleResource.class);
        CIBundleResource ciBundleResource;

        try {
            ciBundleResource = enrichFromSubscribedCIBundle(ciTaxCode, bundle.getId());
        } catch (FeignException.NotFound ignore) {
            ciBundleResource = enrichFromUnsubscribedCIBundle(ciTaxCode, bundle);
        }

        bundleResource.setCiBundleStatus(ciBundleResource.getCiBundleStatus());
        bundleResource.setCiBundleId(ciBundleResource.getCiBundleId());
        bundleResource.setCiRequestId(ciBundleResource.getCiRequestId());
        bundleResource.setCiBundleFeeList(ciBundleResource.getCiBundleFeeList());
        return bundleResource;
    }

    private CIBundleResource enrichFromSubscribedCIBundle(String ciTaxCode, String bundleId) {
        CiBundleDetails ciBundle = this.gecClient.getCIBundle(ciTaxCode, bundleId);
        CIBundleStatus bundleStatus;
        if (isCIBundleEnabled(ciBundle)) {
            bundleStatus = CIBundleStatus.ENABLED;
        } else {
            bundleStatus = CIBundleStatus.ON_REMOVAL;
        }

        return CIBundleResource.builder()
                .ciBundleStatus(bundleStatus)
                .ciBundleId(ciBundle.getIdCIBundle())
                .ciBundleFeeList(getCIBundleFeeList(ciBundle.getAttributes()))
                .build();
    }

    private CIBundleResource enrichFromUnsubscribedCIBundle(String ciTaxCode, Bundle bundle) {
        CIBundleStatus bundleStatus;
        String ciRequestId = null;
        List<CIBundleFee> bundleFees = null;

        PublicBundleRequests bundleRequests = this.gecClient.getCIPublicBundleRequest(ciTaxCode, null, bundle.getId(), 1, 0);
        if (isBundleRequested(bundleRequests)) {
            bundleStatus = CIBundleStatus.REQUESTED;
            PublicBundleRequest request = bundleRequests.getRequestsList().get(0);
            ciRequestId = request.getId();
            bundleFees = getCIBundleFeeList(request.getCiBundleAttributes());
        } else {
            bundleStatus = isBundleExpired(bundle) ? CIBundleStatus.AVAILABLE_EXPIRED : CIBundleStatus.AVAILABLE;
            bundleFees = getBundleTaxonomies(bundle.getTransferCategoryList(), CIBundleFee.class);
        }

        return CIBundleResource.builder()
                .ciBundleStatus(bundleStatus)
                .ciRequestId(ciRequestId)
                .ciBundleFeeList(bundleFees)
                .build();
    }

    private List<CIBundleResource> getWaitingCIPrivateBundleResources(BundleCIOffers bundleOffers) {
        return bundleOffers.getOffers().parallelStream()
                .map(offer -> {
                    Bundle bundle = this.gecClient.getBundleDetail(offer.getIdBundle());
                    CIBundleResource bundleResource = this.modelMapper.map(bundle, CIBundleResource.class);

                    bundleResource.setCiBundleStatus(isBundleExpired(bundle) ? CIBundleStatus.AVAILABLE_EXPIRED : CIBundleStatus.AVAILABLE);
                    bundleResource.setCiOfferId(offer.getId());
                    bundleResource.setCiBundleFeeList(getBundleTaxonomies(bundle.getTransferCategoryList(), CIBundleFee.class));
                    return bundleResource;
                })
                .toList();
    }

    private List<CIBundleResource> getAcceptedCIPrivateBundleResources(CiBundles bundlesByCI) {
        return bundlesByCI.getBundleDetailsList().parallelStream()
                .map(ciBundle -> {
                    Bundle bundle = this.gecClient.getBundleDetail(ciBundle.getIdBundle());
                    CIBundleResource bundleResource = this.modelMapper.map(bundle, CIBundleResource.class);
                    CIBundleStatus bundleStatus;

                    if (isCIBundleEnabled(ciBundle)) {
                        bundleStatus = CIBundleStatus.ENABLED;
                    } else {
                        bundleStatus = CIBundleStatus.ON_REMOVAL;
                    }

                    bundleResource.setCiBundleStatus(bundleStatus);
                    bundleResource.setCiBundleId(ciBundle.getIdCIBundle());
                    bundleResource.setCiBundleFeeList(getCIBundleFeeList(ciBundle.getAttributes()));
                    return bundleResource;
                })
                .toList();
    }

    private boolean isBundleRequested(PublicBundleRequests bundleRequests) {
        return bundleRequests != null && bundleRequests.getPageInfo().getTotalItems() != null && bundleRequests.getPageInfo().getTotalItems() > 0;
    }

    private boolean isCIBundleEnabled(CiBundleDetails ciBundle) {
        return ciBundle.getValidityDateTo() == null || ciBundle.getValidityDateTo().isAfter(LocalDate.now());
    }

    private boolean isBundleExpired(Bundle bundle) {
        return bundle.getValidityDateTo() != null && !bundle.getValidityDateTo().isAfter(LocalDate.now());
    }
}
