package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.*;

import java.util.ArrayList;

public interface GecService {

    Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page);

    Bundles getBundlesByPSP(String pspcode, ArrayList<BundleType> bundleType, String name, Integer limit, Integer page);

    Touchpoints getTouchpoints(Integer limit, Integer page);

    String createPSPBundle(String idpsp, BundleCreate bundle);

    BundlePaymentTypes getPaymenttypes(Integer limit, Integer page);

    }
