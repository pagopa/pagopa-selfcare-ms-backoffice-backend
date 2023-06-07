package it.pagopa.selfcare.pagopa.backoffice.connector.exception;

import lombok.Data;


public class PermissionDeniedException extends RuntimeException{
    public PermissionDeniedException(String message){
        super(message);
    }
}
