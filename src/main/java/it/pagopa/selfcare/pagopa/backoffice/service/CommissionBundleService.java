package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.*;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class CommissionBundleService {

    private static final String BUNDLE_DELETE_SUBSCRIPTION_SUBJECT = "Conferma rimozione da pacchetto";
    private static final String BUNDLE_DELETE_SUBSCRIPTION_BODY = "Ciao, %n%n%n sei stato rimosso dal pacchetto %s.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nPagamenti pagoPa";
    private static final String BUNDLE_CREATE_SUBSCRIPTION_REQUEST_SUBJECT = "Nuova richiesta di attivazione pacchetto commissionale";
    private static final String BUNDLE_CREATE_SUBSCRIPTION_REQUEST_BODY = "Ciao, %n%n%n ci sono nuove richieste di attivazione per il pacchetto commissionale %s.%n%n%n Puoi gestire i tuoi pacchetti qui https://selfcare.platform.pagopa.it/ui/comm-bundles ( https://selfcare.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";
    private static final String BUNDLE_ACCEPT_SUBSCRIPTION_SUBJECT = "Richiesta di adesione confermata";
    private static final String BUNDLE_ACCEPT_SUBSCRIPTION_BODY = "Ciao %n%n%n la tua richiesta di adesione al pacchetto %s è stata accettata.%n%n%n Puoi vedere e gestire il pacchetto da qui ( https://selfcare.platform.pagopa.it/ui/comm-bundles ).%n%n%nA presto,%n%nPagamenti pagoPa";
    private static final String BUNDLE_REJECT_SUBSCRIPTION_SUBJECT = "Richiesta di adesione rifiutata";
    private static final String BUNDLE_REJECT_SUBSCRIPTION_BODY = "Ciao %n%n%n la tua richiesta di adesione al pacchetto %s è stata rifiutata.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nPagamenti pagoPa";

    private static final String VALID_FROM_DATE_FORMAT = "yyyy-MM-dd";

    private final GecClient gecClient;

    private final ModelMapper modelMapper;

    private final TaxonomyService taxonomyService;

    private final LegacyPspCodeUtil legacyPspCodeUtil;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final AwsSesClient awsSesClient;

    @Autowired
    public CommissionBundleService(
            GecClient gecClient,
            ModelMapper modelMapper,
            TaxonomyService taxonomyService,
            LegacyPspCodeUtil legacyPspCodeUtil,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            AwsSesClient awsSesClient
    ) {
        this.gecClient = gecClient;
        this.modelMapper = modelMapper;
        this.taxonomyService = taxonomyService;
        this.legacyPspCodeUtil = legacyPspCodeUtil;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.awsSesClient = awsSesClient;
    }

    public BundlePaymentTypes getBundlesPaymentTypes(Integer limit, Integer page) {
        BundlePaymentTypesDTO dto = gecClient.getPaymenttypes(limit, page);
        return modelMapper.map(dto, BundlePaymentTypes.class);
    }

    public Touchpoints getTouchpoints(Integer limit, Integer page) {
        TouchpointsDTO dto = gecClient.getTouchpoints(limit, page);
        return modelMapper.map(dto, Touchpoints.class);
    }

    public PSPBundlesResource getBundlesByPSP(
            String pspTaxCode,
            List<BundleType> bundleType,
            String name, Integer limit,
            Integer page
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        Bundles bundles = this.gecClient.getBundlesByPSP(pspCode, bundleType, name, limit, page);
        List<PSPBundleResource> bundlesResource = new ArrayList<>();
        if (bundles.getBundleList() != null) {
            bundlesResource = getPSPBundlesResource(bundles);
        }
        return PSPBundlesResource.builder().bundles(bundlesResource).pageInfo(bundles.getPageInfo()).build();

    }

    public BundleCreateResponse createPSPBundle(String pspTaxCode, BundleRequest bundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        return this.gecClient.createPSPBundle(pspCode, bundle);
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
        this.gecClient.updatePSPBundle(pspCode, idBundle, bundle);
    }

    public void deletePSPBundle(String pspTaxCode, String idBundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
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
    public void acceptPublicBundleSubscriptionsByPSP(String pspTaxCode, String requestId, String ciTaxCode, String bundleName) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        this.gecClient.acceptPublicBundleSubscriptionsByPSP(pspCode, requestId);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(BUNDLE_ACCEPT_SUBSCRIPTION_SUBJECT)
                .textBody(String.format(BUNDLE_ACCEPT_SUBSCRIPTION_BODY, bundleName))
                .htmlBodyFileName("acceptPublicBundleSubscriptionEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        awsSesClient.sendEmail(messageDetail);
    }

    /**
     * Retrieve creditor institution paged bundle list of the specified type {@link CIBundleStatus}.
     * The result contains an expanded version of the bundle, using the taxonomy detail extracted
     * from the repository instance
     *
     * @param bundleType the requested type of bundles
     * @param ciTaxCode  creditor institution's tax code, required in case of {@link BundleType#PUBLIC} otherwise is optional and used to filter the results
     * @param limit      page limit parameter
     * @param page       page number parameter
     * @return paged list of bundle resources, expanded with taxonomy data
     */
    public CIBundlesResource getCIBundles(BundleType bundleType, String ciTaxCode, String name, Integer limit, Integer page) {
        List<CIBundleResource> bundlesResource = new ArrayList<>();
        PageInfo pageInfo = new PageInfo();

        List<BundleType> bundleTypes = Collections.singletonList(bundleType);
        if (bundleType.equals(BundleType.GLOBAL) || bundleType.equals(BundleType.PRIVATE)) {
            Bundles bundles = this.gecClient.getBundles(bundleTypes, name, null, limit, page);
            pageInfo = bundles.getPageInfo();
            bundlesResource = getCIBundlesResource(bundles);
        } else if (bundleType.equals(BundleType.PUBLIC)) {
            if (ciTaxCode == null) {
                throw new AppException(AppError.BAD_REQUEST,
                        "Creditor institution's tax code is required to retrieve creditor institution's public bundles");
            }
            String validFrom = LocalDate.now().format(DateTimeFormatter.ofPattern(VALID_FROM_DATE_FORMAT));
            Bundles bundles = gecClient.getBundles(bundleTypes, name, validFrom, limit, page);
            pageInfo = bundles.getPageInfo();
            bundlesResource = getPublicBundleResources(ciTaxCode, bundles);
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
    public void rejectPublicBundleSubscriptionByPSP(String pspTaxCode, String bundleRequestId, String ciTaxCode, String bundleName) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        this.gecClient.rejectPublicBundleSubscriptionByPSP(pspCode, bundleRequestId);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(BUNDLE_REJECT_SUBSCRIPTION_SUBJECT)
                .textBody(String.format(BUNDLE_REJECT_SUBSCRIPTION_BODY, bundleName))
                .htmlBodyFileName("rejectPublicBundleSubscriptionEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        awsSesClient.sendEmail(messageDetail);
    }

    /**
     * Retrieve a paginated list of creditor institution's info that have requested a subscription ({@link PublicBundleSubscriptionStatus#WAITING})
     * or are subscribed ({@link PublicBundleSubscriptionStatus#ACCEPTED}) to a public bundle of a PSP
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param status     the status of the subscription
     * @param ciTaxCode  the creditor institution's tax code
     * @param limit      the size of the page
     * @param page       the page number
     * @return a paginated list of creditor institution's info
     */
    public PublicBundleCISubscriptionsResource getPublicBundleCISubscriptions(
            String idBundle,
            String pspTaxCode,
            PublicBundleSubscriptionStatus status,
            String ciTaxCode,
            Integer limit,
            Integer page
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        PageInfo pageInfo;
        List<CISubscriptionInfo> ciSubscriptionInfoList;

        if (status.equals(PublicBundleSubscriptionStatus.ACCEPTED)) {
            BundleCreditorInstitutionResource acceptedSubscription = this.gecClient
                    .getPublicBundleSubscriptionByPSP(pspCode, idBundle, ciTaxCode, limit, page);

            List<CreditorInstitutionInfo> ciInfoList = getCIInfo(acceptedSubscription);
            ciSubscriptionInfoList = buildCISubscriptionInfoList(ciInfoList, acceptedSubscription);
            pageInfo = acceptedSubscription.getPageInfo();
        } else {
            PublicBundleRequests subscriptionRequest = this.gecClient
                    .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, limit, page);

            List<CreditorInstitutionInfo> ciInfoList = getCIInfo(subscriptionRequest);
            ciSubscriptionInfoList = buildCISubscriptionInfoList(ciInfoList);
            pageInfo = subscriptionRequest.getPageInfo();
        }

        return PublicBundleCISubscriptionsResource.builder()
                .ciSubscriptionInfoList(ciSubscriptionInfoList)
                .pageInfo(pageInfo)
                .build();
    }

    /**
     * Retrieve the detail of a creditor institution's subscription to a public bundle, included the specified taxonomies
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param ciTaxCode  the creditor institution's tax code
     * @param status     the status of the subscription
     * @return the detail of a creditor institution's subscription
     */
    public PublicBundleCISubscriptionsDetail getPublicBundleCISubscriptionsDetail(
            String idBundle,
            String pspTaxCode,
            String ciTaxCode,
            PublicBundleSubscriptionStatus status
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        List<CIBundleFee> ciBundleFeeList = Collections.emptyList();
        String bundleRequestId = null;
        String idCIBundle = null;

        if (status.equals(PublicBundleSubscriptionStatus.ACCEPTED)) {
            CiBundleDetails ciBundleDetails = this.gecClient
                    .getPublicBundleSubscriptionDetailByPSP(pspCode, ciTaxCode, idBundle);

            ciBundleFeeList = getCIBundleFeeList(ciBundleDetails.getAttributes());
            idCIBundle = ciBundleDetails.getIdCIBundle();
        } else {
            PublicBundleRequests subscriptionRequest = this.gecClient
                    .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, 1, 0);

            if (!subscriptionRequest.getRequestsList().isEmpty()) {
                PublicBundleRequest publicBundleRequest = subscriptionRequest.getRequestsList().get(0);

                ciBundleFeeList = getCIBundleFeeList(publicBundleRequest.getCiBundleAttributes());
                bundleRequestId = publicBundleRequest.getId();
            }
        }

        return PublicBundleCISubscriptionsDetail.builder()
                .ciBundleFeeList(ciBundleFeeList)
                .bundleRequestId(bundleRequestId)
                .idCIBundle(idCIBundle)
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

            awsSesClient.sendEmail(messageDetail);
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
                    .subject(BUNDLE_CREATE_SUBSCRIPTION_REQUEST_SUBJECT)
                    .textBody(String.format(BUNDLE_CREATE_SUBSCRIPTION_REQUEST_BODY, bundleName))
                    .htmlBodyFileName("createBundleSubscriptionRequestEmail.html")
                    .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                    .destinationUserType(SelfcareProductUser.ADMIN)
                    .build();

            awsSesClient.sendEmail(messageDetail);
        }
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

    private boolean isOnRemoval(BundleCreditorInstitutionResource acceptedSubscription, CreditorInstitutionInfo ciInfo, LocalDate today) {
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

    private <T extends BundleTaxonomy> List<T> getBundleTaxonomies(List<String> transferCategoryList, Class<T> bundleTaxonomyClazz) {
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

    private List<CIBundleResource> getPublicBundleResources(String ciTaxCode, Bundles bundles) {
        return bundles.getBundleList().parallelStream()
                .map(bundle -> buildCIPublicBundle(ciTaxCode, bundle))
                .toList();
    }

    private CIBundleResource buildCIPublicBundle(String ciTaxCode, Bundle bundle) {
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
        bundleResource.setCiBundleFeeList(ciBundleResource.getCiBundleFeeList());
        return bundleResource;
    }

    private CIBundleResource enrichFromSubscribedCIBundle(String ciTaxCode, String bundleId) {
        CiBundleDetails ciBundle = this.gecClient.getCIBundle(ciTaxCode, bundleId);
        CIBundleStatus bundleStatus;
        if (ciBundle.getValidityDateTo() == null || ciBundle.getValidityDateTo().isAfter(LocalDate.now())) {
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
            bundleStatus = CIBundleStatus.AVAILABLE;
            bundleFees = getBundleTaxonomies(bundle.getTransferCategoryList(), CIBundleFee.class);
        }

        return CIBundleResource.builder()
                .ciBundleStatus(bundleStatus)
                .ciRequestId(ciRequestId)
                .ciBundleFeeList(bundleFees)
                .build();
    }

    private boolean isBundleRequested(PublicBundleRequests bundleRequests) {
        return bundleRequests != null && bundleRequests.getPageInfo().getTotalItems() != null && bundleRequests.getPageInfo().getTotalItems() > 0;
    }
}
