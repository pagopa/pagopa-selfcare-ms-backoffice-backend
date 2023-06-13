package it.pagopa.selfcare.pagopa.backoffice.connector.api;

public interface JiraServiceManagerConnector {

    void setReqTypeTaskId(String reqTypeTaskId);
    String createTicket(String summary, String description);
}
