package it.pagopa.selfcare.pagopa.backoffice.connector.model.channel;

public enum Protocol {
    HTTPS("HTTPS"),
    HTTP("HTTP");
    private final String value;
    Protocol(String value) {
        this.value = value;
    }

}