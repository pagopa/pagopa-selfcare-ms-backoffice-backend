package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlePaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundleFee;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CISubscriptionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleCISubscriptionsDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleCISubscriptionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleSubscriptionStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Touchpoints;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleAttribute;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiFiscalCodeList;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspCiBundleAttribute;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspRequests;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommissionBundleService {

    private final GecClient gecClient;

    private final ModelMapper modelMapper;

    private final TaxonomyService taxonomyService;

    private final LegacyPspCodeUtil legacyPspCodeUtil;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @Autowired
    public CommissionBundleService(
            GecClient gecClient,
            ModelMapper modelMapper,
            TaxonomyService taxonomyService,
            LegacyPspCodeUtil legacyPspCodeUtil,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient
    ) {
        this.gecClient = gecClient;
        this.modelMapper = modelMapper;
        this.taxonomyService = taxonomyService;
        this.legacyPspCodeUtil = legacyPspCodeUtil;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
    }

    public BundlePaymentTypes getBundlesPaymentTypes(Integer limit, Integer page) {
        BundlePaymentTypesDTO dto = gecClient.getPaymenttypes(limit, page);
        return modelMapper.map(dto, BundlePaymentTypes.class);
    }

    public Touchpoints getTouchpoints(Integer limit, Integer page) {
        TouchpointsDTO dto = gecClient.getTouchpoints(limit, page);
        return modelMapper.map(dto, Touchpoints.class);
    }

    public BundlesResource getBundlesByPSP(
            String pspTaxCode,
            List<BundleType> bundleType,
            String name, Integer limit,
            Integer page
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        Bundles bundles = this.gecClient.getBundlesByPSP(pspCode, bundleType, name, limit, page);
        return getBundlesResource(bundles);
    }

    private BundlesResource getBundlesResource(Bundles bundles) {
        List<BundleResource> bundleResources = bundles.getBundles() != null ?
                bundles.getBundles().stream().map(bundle -> {
                    BundleResource bundleResource = new BundleResource();
                    BeanUtils.copyProperties(bundle, bundleResource);
                    bundleResource.setTransferCategoryList(
                            this.taxonomyService.getTaxonomiesByCodes(bundle.getTransferCategoryList())
                    );
                    return bundleResource;
                }).toList() :
                new ArrayList<>();
        return BundlesResource.builder().bundles(bundleResources).pageInfo(bundles.getPageInfo()).build();
    }

    public BundleCreateResponse createPSPBundle(String pspTaxCode, BundleRequest bundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        return this.gecClient.createPSPBundle(pspCode, bundle);
    }

    public BundleResource getBundleDetailByPSP(String pspTaxCode, String idBundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        Bundle bundle = this.gecClient.getBundleDetailByPSP(pspCode, idBundle);
        BundleResource bundleResource = new BundleResource();
        BeanUtils.copyProperties(bundle, bundleResource);
        bundleResource.setTransferCategoryList(this.taxonomyService.getTaxonomiesByCodes(bundle.getTransferCategoryList()));
        return bundleResource;
    }

    public void updatePSPBundle(String pspTaxCode, String idBundle, BundleRequest bundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        this.gecClient.updatePSPBundle(pspCode, idBundle, bundle);
    }

    public void deletePSPBundle(String pspTaxCode, String idBundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        this.gecClient.deletePSPBundle(pspCode, idBundle);
    }

    /**
     * Retrieve the PSP code with the psp tax code and accept the list of EC subscription requests to a public bundle
     * by invoking the {@link GecClient}
     *
     * @param pspTaxCode          the tax code of the PSP that owns the public bundle
     * @param bundleRequestIdList the list of bundle request id to be accepted
     */
    public void acceptPublicBundleSubscriptionsByPSP(String pspTaxCode, List<String> bundleRequestIdList) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        for (String requestId : bundleRequestIdList) {
            this.gecClient.acceptPublicBundleSubscriptionsByPSP(pspCode, requestId);
        }
    }

    /**
     * Retrieve creditor institution paged bundle list, using optionally a filter by creditor institution tax code,
     * using a dedicate API. the result contains an expanded version of the bundle, using the taxonomy detail extracted
     * from the repository instance
     *
     * @param cisTaxCode optional parameter used for filter by creditor institution tax code
     * @param limit      page limit parameter
     * @param page       page number parameter
     * @return paged list of bundle resources, expanded with taxonomy data
     */
    public BundlesResource getCisBundles(List<BundleType> bundleType, String cisTaxCode, String name, Integer limit, Integer page) {
        Bundles bundles = cisTaxCode != null ?
                gecClient.getBundlesByCI(cisTaxCode, limit, page) : gecClient.getBundles(bundleType, name, limit, page);
        if (cisTaxCode != null && (bundleType != null || name != null)) {
            bundles.setBundles(bundles.getBundles().stream().filter(
                            item -> (bundleType == null || bundleType.contains(item.getType())) &&
                                    (name == null || item.getName().toLowerCase().contains(name.toLowerCase())))
                    .toList());
        }
        return getBundlesResource(bundles);
    }

    /**
     * Reject a subscription requests to a public bundle
     *
     * @param pspTaxCode      the tax code of the PSP that owns the public bundle
     * @param bundleRequestId the request id to be rejected
     */
    public void rejectPublicBundleSubscriptionByPSP(String pspTaxCode, String bundleRequestId) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        this.gecClient.rejectPublicBundleSubscriptionByPSP(pspCode, bundleRequestId);
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
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);

        if (status.equals(PublicBundleSubscriptionStatus.ACCEPTED)) {
            CiFiscalCodeList acceptedSubscription = this.gecClient
                    .getPublicBundleSubscriptionByPSP(pspCode, idBundle, ciTaxCode, limit, page);

            List<CreditorInstitutionInfo> ciInfoList = this.apiConfigSelfcareIntegrationClient
                    .getCreditorInstitutionInfo(acceptedSubscription.getCiTaxCodeList());

            return PublicBundleCISubscriptionsResource.builder()
                    .ciSubscriptionInfoList(
                            ciInfoList.parallelStream()
                                    .map(ciInfo -> this.modelMapper.map(ciInfo, CISubscriptionInfo.class))
                                    .toList()
                    )
                    .pageInfo(null) // TODO missing pagination info
                    .build();
        }

        PspRequests subscriptionRequest = this.gecClient
                .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, limit, page);

        List<String> taxCodeList = subscriptionRequest.getRequestsList().parallelStream()
                .map(PspBundleRequest::getCiFiscalCode)
                .toList();
        List<CreditorInstitutionInfo> ciInfoList = this.apiConfigSelfcareIntegrationClient
                .getCreditorInstitutionInfo(taxCodeList);

        return PublicBundleCISubscriptionsResource.builder()
                .ciSubscriptionInfoList(
                        ciInfoList.parallelStream()
                                .map(ciInfo -> this.modelMapper.map(ciInfo, CISubscriptionInfo.class))
                                .toList()
                )
                .pageInfo(subscriptionRequest.getPageInfo())
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
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);

        if (status.equals(PublicBundleSubscriptionStatus.ACCEPTED)) {
            CiBundleDetails ciBundleDetails = this.gecClient
                    .getPublicBundleSubscriptionDetailByPSP(pspCode, ciTaxCode, idBundle);

            List<String> transferCategoryList = ciBundleDetails.getAttributes().parallelStream()
                    .map(CiBundleAttribute::getTransferCategory)
                    .toList();

            List<Taxonomy> taxonomies = this.taxonomyService.getTaxonomiesByCodes(transferCategoryList);

            return PublicBundleCISubscriptionsDetail.builder()
                    .ciBundleFeeList(
                            ciBundleDetails.getAttributes().parallelStream()
                                    .map(ciBundleAttribute -> buildCIBundleFee(
                                            ciBundleAttribute.getMaxPaymentAmount(),
                                            ciBundleAttribute.getTransferCategory(),
                                            taxonomies)
                                    )
                                    .toList()
                    )
                    .build();
        }

        PspRequests subscriptionRequest = this.gecClient
                .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, 1, null);

        PspBundleRequest pspBundleRequest = subscriptionRequest.getRequestsList().get(0);
        List<String> transferCategoryList = pspBundleRequest
                .getCiBundleAttributes().parallelStream()
                .map(PspCiBundleAttribute::getTransferCategory)
                .toList();

        List<Taxonomy> taxonomies = this.taxonomyService.getTaxonomiesByCodes(transferCategoryList);

        return PublicBundleCISubscriptionsDetail.builder()
                .ciBundleFeeList(
                        pspBundleRequest.getCiBundleAttributes().parallelStream()
                                .map(ciBundleAttribute -> buildCIBundleFee(
                                        ciBundleAttribute.getMaxPaymentAmount(),
                                        ciBundleAttribute.getTransferCategory(),
                                        taxonomies)
                                )
                                .toList()
                )
                .bundleRequestId(pspBundleRequest.getId())
                .build();
    }

    private CIBundleFee buildCIBundleFee(Long paymentAmount, String serviceType, List<Taxonomy> taxonomies) {
        return CIBundleFee.builder()
                .paymentAmount(paymentAmount)
                .serviceType(serviceType)
                .specificBuiltInData(taxonomies.stream()
                        .filter(taxonomy -> taxonomy.getServiceType().equals(serviceType))
                        .findFirst()
                        .orElse(new Taxonomy())
                        .getSpecificBuiltInData())
                .build();
    }
}
