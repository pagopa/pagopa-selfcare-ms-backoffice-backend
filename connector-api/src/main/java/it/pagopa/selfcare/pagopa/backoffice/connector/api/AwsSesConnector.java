package it.pagopa.selfcare.pagopa.backoffice.connector.api;

public interface AwsSesConnector {

    String sendEmail(String to, String subject, String body);

}
