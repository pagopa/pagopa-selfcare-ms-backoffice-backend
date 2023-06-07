package it.pagopa.selfcare.pagopa.backoffice.connector.api;

public interface JiraServiceManagerConnector {

    String createTicket(String summary, String description);
}
