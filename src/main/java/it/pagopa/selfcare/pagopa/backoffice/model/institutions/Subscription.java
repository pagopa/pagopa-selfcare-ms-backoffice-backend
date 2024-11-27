package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType.CI;
import static it.pagopa.selfcare.pagopa.backoffice.model.institutions.RoleType.PSP;

/**
 * Enum of all the subscription that can be requested via Backoffice
 */
@Getter
@AllArgsConstructor
public enum Subscription {

    NODOAUTH("/products/nodo-auth", "Connessione con nodo", "nodauth-", null, null, false),
    GPD("/products/debt-positions", "GPD - Posizioni Debitorie", "gdp-", CI, "gpd", true),
    GPD_REP("/products/product-gpd-reporting", "GPD - Gestione flussi di rendicontazione", "gpdrep-", CI, "gpd", true),
    GPD_PAY("/products/gpd-payments-rest-aks", "GPD - Recupero ricevute", "gpdpay-", CI, "gpd", true),
    BIZ("/products/bizevents", "BIZ - Recupero ricevute Ente Creditore", "biz-", CI, null, false),
    FDR_ORG("/products/fdr-org", "FdR - Flussi di Rendicontazione (EC)", "fdrorg-", CI, "fdr", true),
    FDR_PSP("/products/fdr-psp", "FdR - Flussi di Rendicontazione (PSP)", "fdrpsp-", PSP, "fdr", true),
    BO_EXT_EC("/apis/%s-backoffice-external-ec-api-v1", "Backoffice External (EC)", "selfcareboexternalec-", CI, "backoffice_external", false),
    BO_EXT_PSP("/apis/%s-backoffice-external-psp-api-v1", "Backoffice External (PSP)", "selfcareboexternalpsp-", PSP, "backoffice_external", false),
    PRINT_NOTICE("/products/pagopa_notices_service_external", "Stampa Avvisi", "printnotice-", CI, null, false),
    ACA("/products/aca", "ACA - Conferimento delle posizioni debitorie", "aca-", CI, "aca", true);

    private final String scope;
    private final String displayName;
    private final String prefixId;
    private final RoleType allowedInstitutionType;
    private final String authDomain;
    private final boolean authDelegationConfigRequired;

    public static Subscription fromPrefix(String prefix) {
        return Arrays.stream(Subscription.values())
                .filter(elem -> prefix.equals(elem.prefixId))
                .findFirst()
                .orElseThrow(() -> new AppException(AppError.SUBSCRIPTION_PREFIX_NOT_FOUND, prefix));
    }
}
