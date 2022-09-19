package it.pagopa.selfcare.pagopa.backoffice.connector.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
