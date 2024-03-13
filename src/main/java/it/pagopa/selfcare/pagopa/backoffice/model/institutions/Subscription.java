package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum of all the subscription that can be requested via Backoffice
 */
@Getter
@AllArgsConstructor
public enum Subscription {

    NODOAUTH("/products/nodo-auth", "Connessione con nodo", "nodauth-"),
    GPD("/products/debt-positions", "GPD - Posizioni Debitorie", "gdp-"),
    GPD_REP("/products/product-gpd-reporting", "GPD - Gestione flussi di rendicontazione", "gpdrep-"),
    GPD_PAY("/products/gpd-payments-rest-aks", "GPD - Recupero ricevute", "gpdpay-"),
    BIZ("/products/bizevents", "BIZ - Recupero ricevute Ente Creditore", "biz-"),
    FDR_ORG("/products/fdr-org", "FdR - Flussi di Rendicontazione (EC)", "fdrorg-"),
    FDR_PSP("/products/fdr-psp", "FdR - Flussi di Rendicontazione (PSP)", "fdrpsp-"),
    BO_EXT("/products/selfcare-bo-external", "Backoffice External", "selfcareboexternal-");

    private final String scope;
    private final String displayName;
    private final String prefixId;

}
