package it.pagopa.selfcare.pagopa.backoffice.core;

public interface AwsSesService {

   String sendEmail( String subject, String body,String...to);

}
