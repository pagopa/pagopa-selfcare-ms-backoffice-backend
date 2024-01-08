package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Subscription {

    NODOAUTH("/products/nodo-auth", "Connessione con nodo", "nodauth-"),
    BIZ("/products/bizevents", "Recupero Ricevuta", "biz-"),
    GPD("/products/debt-positions", "Integrazione Asincrona - Posizioni Debitorie", "gdp-"),
    GPD_REP("/products/product-gpd-reporting", "Integrazione Asincrona - Gestione flussi di rendicontazione", "gpdrep-"),
    GPD_PAY("/products/gpd-payments-rest-aks", "Integrazione Asincrona - Ricevute", "gpdpay-"),
    FDR_ORG("/products/fdr-org", "FdR - Flussi di rendicontazione ORG", "fdrorg-"),
    FDR_PSP("/products/fdr-psp", "FdR - Flussi di rendicontazione PSP", "fdrpsp-");

    private final String scope;
    private final String displayName;
    private final String prefixId;

}
