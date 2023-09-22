package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;

public interface GecService {

    Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page, String xRequestId);

    Bundles getBundlesByPSP(String pspcode, Integer limit, Integer page, String xRequestId);

    Touchpoints getTouchpoints(Integer limit, Integer page, String xRequestId);

    }
