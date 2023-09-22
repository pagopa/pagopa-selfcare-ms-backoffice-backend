package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;

public interface GecConnector {
    Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page, String xRequestId);

    Bundles getBundlesByPSP(String pspcode, Integer limit, Integer page, String xRequestId);

    Touchpoints getTouchpoints(Integer limit, Integer page, String xRequestId);
    
}
