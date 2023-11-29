package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

public enum PaymentModel {

    IMMEDIATE("IMMEDIATE"),
    IMMEDIATE_MULTIBENEFICIARY("IMMEDIATE_MULTIBENEFICIARY"),
    DEFERRED("DEFERRED"),
    ACTIVATED_AT_PSP("ACTIVATED_AT_PSP");
    private final String value;

    PaymentModel(String value) {
        this.value = value;
    }
}
