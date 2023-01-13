package it.pagopa.selfcare.pagopa.backoffice.web.model.subscriptions;

public enum Subscription {

    NODOAUTH("/products/nodo-auth", "node_auth", "nodauth-"),
    GPD("/products/debt-positions", "debt_positions", "gdp-"),
    BIZ("/products/bizevents", "bizevents", "bes-");

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

    private Subscription(String scope, String displayName, String prefixId) {
        this.scope = scope;
        this.displayName = displayName;
        this.prefixId = prefixId;
    }


}
