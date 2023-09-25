package it.pagopa.selfcare.pagopa.backoffice.connector.exception;

public class PermissionDeniedException extends RuntimeException{
    public PermissionDeniedException(String message){
        super(message);
    }
}
