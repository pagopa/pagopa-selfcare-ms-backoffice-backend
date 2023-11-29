package it.pagopa.selfcare.pagopa.backoffice.exception;

public class PermissionDeniedException extends RuntimeException{
    public PermissionDeniedException(String message){
        super(message);
    }
}
