package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

public enum Subscription {

    NODOAUTH("/products/nodo-auth", "Connessione con nodo", "nodauth-"),
    GPD("/products/debt-positions", "Integrazione Asincrona - Posizioni Debitorie", "gdp-"),
    BIZ("/products/bizevents", "Recupero Ricevuta", "bes-"),
    GPD_REP("/products/product-gpd-reporting", "Integrazione Asincrona - Gestione flussi di rendicontazione", "gpdrep-"),
    GPD_PAY("/products/gpd-payments-rest-aks", "Integrazione Asincrona - Ricevute", "gpdpayra-");

    private final String scope;
    private final String displayName;
    private final String prefixId;

    public String getPrefixId() {
        return prefixId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getScope() {
        return scope;
    }

    Subscription(String scope, String displayName, String prefixId) {
        this.scope = scope;
        this.displayName = displayName;
        this.prefixId = prefixId;
    }
}
