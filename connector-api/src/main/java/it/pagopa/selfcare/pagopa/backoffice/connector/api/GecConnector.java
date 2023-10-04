package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;

import java.util.ArrayList;

public interface GecConnector {
    Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page);

    Bundles getBundlesByPSP(String pspcode, ArrayList<BundleType> bundleType, String name, Integer limit, Integer page);

    Touchpoints getTouchpoints(Integer limit, Integer page);

    String createPSPBundle(String idpsp, BundleCreate bundle);
    
}
