package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.GecConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class GecServiceImpl implements GecService {
     private final GecConnector gecConnector;

    @Autowired
    public GecServiceImpl(GecConnector gecConnector) {
        this.gecConnector = gecConnector;
    }

    @Override
    public Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page) {
        Bundles response = gecConnector.getBundlesByCI(cifiscalcode, limit, page);
        return response;
    }
    @Override
    public Touchpoints getTouchpoints(Integer limit, Integer page){
        Touchpoints response = gecConnector.getTouchpoints(limit, page);
        return response;
    }
    @Override
    public Bundles getBundlesByPSP(String pspcode, ArrayList<BundleType> bundleType, String name, Integer limit, Integer page){
        Bundles response = gecConnector.getBundlesByPSP(pspcode, bundleType, name, limit, page);
        return response;
    }
    @Override
    public String createPSPBundle(String idpsp, BundleCreate bundle){
        String response = gecConnector.createPSPBundle(idpsp, bundle);
        return response;
    }
    @Override
    @Cacheable(value = "GecPaymentTypes", key = "#limit.toString() + '-' + #page.toString()")
    public BundlePaymentTypes getPaymenttypes(Integer limit, Integer page){
        BundlePaymentTypes response = gecConnector.getPaymenttypes(limit, page);
        return response;
    }

}
