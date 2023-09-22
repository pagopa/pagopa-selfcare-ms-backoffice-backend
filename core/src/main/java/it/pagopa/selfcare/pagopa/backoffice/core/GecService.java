package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Touchpoints;

import java.util.ArrayList;

public interface GecService {

    Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page, String xRequestId);

    Bundles getBundlesByPSP(String pspcode, ArrayList<BundleType> bundleType, String name, Integer limit, Integer page, String xRequestId);

    Touchpoints getTouchpoints(Integer limit, Integer page, String xRequestId);

    }
