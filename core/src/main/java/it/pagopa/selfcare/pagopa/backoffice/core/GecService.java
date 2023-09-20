package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;

public interface GecService {

    Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page, String xRequestId);

    }
