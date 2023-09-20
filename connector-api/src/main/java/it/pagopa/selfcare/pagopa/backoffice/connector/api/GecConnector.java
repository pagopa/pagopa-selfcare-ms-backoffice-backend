package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;

public interface GecConnector {
    Bundles getBundlesByCI(String cifiscalcode, Integer limit, Integer page, String xRequestId);
    
}
