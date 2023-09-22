package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.GecConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GecServiceImpl implements GecService {
     private final GecConnector gecConnector;

    @Autowired
    public GecServiceImpl(GecConnector gecConnector) {
        this.gecConnector = gecConnector;
    }

    @Override
    public Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page, String xRequestId) {
        log.trace("getBundlesByCI start");
        Bundles response = gecConnector.getBundlesByCI(cifiscalcode, limit, page, xRequestId);
        log.debug("getBundlesByCI result = {}", response);
        log.trace("getBundlesByCI end");
        return response;
    }
    @Override
    public Touchpoints getTouchpoints(Integer limit, Integer page, String xRequestId){
        log.trace("getTouchpoints start");
        Touchpoints response = gecConnector.getTouchpoints(limit, page, xRequestId);
        log.debug("getTouchpoints result = {}", response);
        log.trace("getTouchpoints end");
        return response;
    }
    @Override
    public Bundles getBundlesByPSP(String pspcode, Integer limit, Integer page, String xRequestId){
        log.trace("getBundlesByPSP start");
        Bundles response = gecConnector.getBundlesByPSP(pspcode, limit, page, xRequestId);
        log.debug("getBundlesByPSP result = {}", response);
        log.trace("getBundlesByPSP end");
        return response;
    }

}
