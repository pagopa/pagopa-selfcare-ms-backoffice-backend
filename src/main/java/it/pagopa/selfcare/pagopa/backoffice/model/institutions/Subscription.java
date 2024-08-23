package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Enum of all the subscription that can be requested via Backoffice
 */
@Getter
@AllArgsConstructor
public enum Subscription {

    NODOAUTH("/products/nodo-auth", "Connessione con nodo", "nodauth-", null),
    GPD("/products/debt-positions", "GPD - Posizioni Debitorie", "gdp-", "gpd"),
    GPD_REP("/products/product-gpd-reporting", "GPD - Gestione flussi di rendicontazione", "gpdrep-", "gpd"),
    GPD_PAY("/products/gpd-payments-rest-aks", "GPD - Recupero ricevute", "gpdpay-", "gpd"),
    BIZ("/products/bizevents", "BIZ - Recupero ricevute Ente Creditore", "biz-", null),
    FDR_ORG("/products/fdr-org", "FdR - Flussi di Rendicontazione (EC)", "fdrorg-", "fdr"),
    FDR_PSP("/products/fdr-psp", "FdR - Flussi di Rendicontazione (PSP)", "fdrpsp-", "fdr"),
    BO_EXT_EC("/apis/%s-backoffice-external-ec-api-v1", "Backoffice External (EC)", "selfcareboexternalec-", "backoffice_external"),
    BO_EXT_PSP("/apis/%s-backoffice-external-psp-api-v1", "Backoffice External (PSP)", "selfcareboexternalpsp-", "backoffice_external"),
    PRINT_NOTICE("/products/pagopa_notices_service_external", "Stampa Avvisi", "printnotice-", null),
    ACA("/products/aca", "ACA - paCreatePosition", "aca-", "aca");

    private final String scope;
    private final String displayName;
    private final String prefixId;
    private final String authDomain;

    public static Subscription fromPrefix(String prefix) {
        return Arrays.stream(Subscription.values())
                .filter(elem -> prefix.equals(elem.prefixId))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.SUBSCRIPTION_PREFIX_NOT_FOUND, prefix));
    }
}
