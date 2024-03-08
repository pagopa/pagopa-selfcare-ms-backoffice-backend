package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.*;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.*;
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

    @Autowired
    public CommissionBundleService(GecClient gecClient, ModelMapper modelMapper, TaxonomyService taxonomyService) {
        this.gecClient = gecClient;
        this.modelMapper = modelMapper;
        this.taxonomyService = taxonomyService;
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
            String pspCode, List<BundleType> bundleType, String name, Integer limit, Integer page) {
        Bundles bundles = gecClient.getBundlesByPSP(pspCode, bundleType, name, limit, page);
        List<BundleResource> bundleResources = bundles.getBundles() != null ?
                bundles.getBundles().stream().map(bundle -> {
                    BundleResource bundleResource = new BundleResource();
                    BeanUtils.copyProperties(bundle, bundleResource);
                    bundleResource.setTransferCategoryList(
                            taxonomyService.getTaxonomiesByCodes(bundle.getTransferCategoryList())
                    );
                    return bundleResource;
                }).toList() :
                new ArrayList<>();
        return BundlesResource.builder().bundles(bundleResources).pageInfo(bundles.getPageInfo()).build();
    }

    public BundleCreateResponse createPSPBundle(String pspCode, BundleRequest bundle) {
        return gecClient.createPSPBundle(pspCode, bundle);
    }

    public BundleResource getBundleDetailByPSP(String pspCode, String idBundle){
        Bundle bundle = gecClient.getBundleDetailByPSP(pspCode, idBundle);
        BundleResource bundleResource = new BundleResource();
        BeanUtils.copyProperties(bundle, bundleResource);
        bundleResource.setTransferCategoryList(taxonomyService.getTaxonomiesByCodes(bundle.getTransferCategoryList()));
        return bundleResource;
    }

    public void updatePSPBundle(String pspCode, String idBundle, BundleRequest bundle){
        gecClient.updatePSPBundle(pspCode, idBundle, bundle);
    }

    public void deletePSPBundle(String pspCode, String idBundle){
        gecClient.deletePSPBundle(pspCode, idBundle);
    }

    /**
     * Accept the private bundle offer with the provided id.
     * The provided tax code identifies the creditor institution that accept the offer
     *
     * @param ciTaxCode the tax code of the creditor institution
     * @param idBundleOffer th id of the bundle offer
     * @return the id of the accepted private bundle
     */
    public CIBundleId ciAcceptPrivateBundleOffer(String ciTaxCode, String idBundleOffer) {
        return gecClient.ciAcceptPrivateBundleOffer(ciTaxCode, idBundleOffer);
    }

    /**
     * Remove the subscription of a creditor institution to a bundle
     *
     * @param ciTaxCode the creditor institution tax code
     * @param idBundle the bundle id
     */
    public void removeCIBundle(String ciTaxCode, String idBundle){
        gecClient.removeCIBundle(ciTaxCode, idBundle);
    }
}
