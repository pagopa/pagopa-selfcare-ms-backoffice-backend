package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import org.springframework.stereotype.Service;

public interface JiraServiceManagerConnector {

    String createTicket(String summary, String description);
}
