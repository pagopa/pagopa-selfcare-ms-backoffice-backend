package it.pagopa.selfcare.pagopa.backoffice.core;

public interface AwsSesService {

   String sendEmail(String to, String subject, String body);

}
