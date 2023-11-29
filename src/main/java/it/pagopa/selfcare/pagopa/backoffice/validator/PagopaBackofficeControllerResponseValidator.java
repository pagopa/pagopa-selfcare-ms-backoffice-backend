package it.pagopa.selfcare.pagopa.backoffice.validator;

import it.pagopa.selfcare.pagopa.backoffice.exception.ResponseValidationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
@Slf4j
@Aspect
@Component
public class PagopaBackofficeControllerResponseValidator {


    private final Validator validator;


    public PagopaBackofficeControllerResponseValidator(Validator validator) {
        Assert.notNull(validator, "Validator is required");
        this.validator = validator;
    }


    @AfterReturning(pointcut = "controllersPointcut()", returning = "result")
    public void validateResponse(JoinPoint joinPoint, Object result) {
        if (result != null) {
            if (Collection.class.isAssignableFrom(result.getClass())) {
                ((Collection<?>) result).forEach(this::validate);
            } else {
                validate(result);
            }
        }
    }

    private void validate(Object result) {
        Set<ConstraintViolation<Object>> validationResults = validator.validate(result);
        if (!validationResults.isEmpty()) {
            Map<String, List<String>> errorMessage = new HashMap<>();
            validationResults.forEach(error -> {
                String fieldName = error.getPropertyPath().toString();
                errorMessage.computeIfAbsent(fieldName, s -> new ArrayList<>())
                        .add(error.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " constraint violation");
            });
            throw new ResponseValidationException(errorMessage.toString());
        }
    }


    @Pointcut("execution(* it.pagopa.selfcare.pagopa.backoffice.web.controller.*.*(..))")
    public void controllersPointcut() {
        // Do nothing because is a pointcut
    }

}
