package it.pagopa.selfcare.pagopa.backoffice.handler;

import com.azure.core.exception.HttpResponseException;
import com.azure.core.management.exception.ManagementException;
import it.pagopa.selfcare.pagopa.backoffice.model.Problem;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ProblemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AzureManagementExceptionHandler {

    static final String UNHANDLED_EXCEPTION = "unhandled exception: ";

    public AzureManagementExceptionHandler(){
        log.trace("Initializing {}", AzureManagementExceptionHandler.class.getSimpleName());
    }
    
    @ExceptionHandler({ManagementException.class})
    ResponseEntity<Problem> handleManagementException(HttpResponseException e) {
        if (e.getResponse().getStatusCode() == 404) {
            return ProblemMapper.toResponseEntity(new Problem(HttpStatus.NOT_FOUND, e.getMessage()));
        } else
            return ProblemMapper.toResponseEntity(new Problem(HttpStatus.valueOf(e.getResponse().getStatusCode()), e.getMessage()));
    }
}
