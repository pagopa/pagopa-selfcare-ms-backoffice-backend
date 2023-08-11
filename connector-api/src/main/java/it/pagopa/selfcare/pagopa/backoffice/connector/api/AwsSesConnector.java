package it.pagopa.selfcare.pagopa.backoffice.connector.api;

public interface AwsSesConnector {

    String sendEmail( String subject, String body,String...to);

}
