package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlePaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Touchpoints;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
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

    @Autowired
    public CommissionBundleService(
            GecClient gecClient,
            ModelMapper modelMapper,
            TaxonomyService taxonomyService,
            LegacyPspCodeUtil legacyPspCodeUtil) {
        this.gecClient = gecClient;
        this.modelMapper = modelMapper;
        this.taxonomyService = taxonomyService;
        this.legacyPspCodeUtil = legacyPspCodeUtil;
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
     * @param pspTaxCode the tax code of the PSP that owns the public bundle
     * @param bundleRequestIdList the list of bundle request id to be accepted
     */
    public void acceptPublicBundleSubscriptionsByPSP(String pspTaxCode, List<String> bundleRequestIdList) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        for (String requestId : bundleRequestIdList) {
            this.gecClient.acceptPublicBundleSubscriptionsByPSP(pspCode, requestId);
        }
    }
}
