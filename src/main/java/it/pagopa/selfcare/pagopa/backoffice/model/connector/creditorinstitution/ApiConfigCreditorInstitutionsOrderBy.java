package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

/**
 * Enum used by ApiConfig as orderBy type parameter for API that query against Creditor Institution entity
 */
public enum ApiConfigCreditorInstitutionsOrderBy {

    // filter by field idDominio
    CODE,
    // filter by field ragioneSociale
    NAME;
}
