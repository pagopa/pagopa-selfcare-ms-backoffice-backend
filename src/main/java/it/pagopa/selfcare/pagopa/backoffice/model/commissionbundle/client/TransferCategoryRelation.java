package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client;

import lombok.Getter;

/**
 * Enum that describe the possible transfer category relation types
 */
@Getter
public enum TransferCategoryRelation {
    EQUAL("EQUAL"),
    NOT_EQUAL("NOT_EQUAL");

    private final String value;

    TransferCategoryRelation(final String transferCategoryRelation) {
        this.value = transferCategoryRelation;
    }
}
