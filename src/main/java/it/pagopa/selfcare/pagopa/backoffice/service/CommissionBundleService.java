package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.*;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommissionBundleService {

    @Autowired
    private GecClient gecClient;

    @Autowired
    private ModelMapper modelMapper;

    public BundlePaymentTypes getBundlesPaymentTypes(Integer limit, Integer page) {
        BundlePaymentTypesDTO dto = gecClient.getPaymenttypes(limit, page);
        return modelMapper.map(dto, BundlePaymentTypes.class);
    }

    public Touchpoints getTouchpoints(Integer limit, Integer page) {
        TouchpointsDTO dto = gecClient.getTouchpoints(limit, page);
        return modelMapper.map(dto, Touchpoints.class);
    }

    public Bundles getBundlesByPSP(String pspCode, List<BundleType> bundleType, String name, Integer limit, Integer page) {
        return gecClient.getBundlesByPSP(pspCode, bundleType, name, limit, page);
    }

    public String createPSPBundle(String pspCode, Bundle bundle) {
        return gecClient.createPSPBundle(pspCode, bundle);
    }
}
