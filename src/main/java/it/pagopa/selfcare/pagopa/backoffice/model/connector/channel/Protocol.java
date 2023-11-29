package it.pagopa.selfcare.pagopa.backoffice.model.connector.channel;

public enum Protocol {
    HTTPS("HTTPS"),
    HTTP("HTTP");
    private final String value;
    Protocol(String value) {
        this.value = value;
    }

}
