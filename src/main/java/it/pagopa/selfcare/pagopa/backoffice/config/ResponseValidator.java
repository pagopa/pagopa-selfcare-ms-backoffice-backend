package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Aspect
@Component
public class ResponseValidator {

    private final Validator validator;

    public ResponseValidator(Validator validator) {
        this.validator = validator;
    }


    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {
        // all rest controllers
    }


    @Pointcut("execution(* it.pagopa.selfcare.pagopa.backoffice.client.*.*(..))")
    public void dependencyClient() {
        // all rest controllers
    }


    /**
     * This method validates the response annotated with the {@link javax.validation.constraints}
     *
     * @param joinPoint not used
     * @param result    the response to validate
     */
    @AfterReturning(pointcut = "restController()", returning = "result")
    public void validateResponse(JoinPoint joinPoint, Object result) {
        if(result instanceof ResponseEntity) {
            validateResponse(((ResponseEntity<?>) result).getBody(), joinPoint);
        }
        else{
            validateResponse(result, joinPoint);
        }
    }

    @AfterReturning(pointcut = "dependencyClient()", returning = "result")
    public void validateClientResponse(JoinPoint joinPoint, Object result) {
        validateResponse(result, joinPoint);
    }

    private void validateResponse(Object response, JoinPoint joinPoint) {
        if(response != null) {
            Set<ConstraintViolation<Object>> validationResults = validator.validate(response);

            if(!validationResults.isEmpty()) {
                var sb = new StringBuilder();
                for (ConstraintViolation<Object> error : validationResults) {
                    sb.append(error.getPropertyPath()).append(" ").append(error.getMessage()).append(". ");
                }
                var msg = StringUtils.chop(sb.toString());
                if(joinPoint.getSignature().toShortString().toLowerCase().contains("controller")) {
                    throw new AppException(AppError.OUR_RESPONSE_NOT_VALID, msg);
                } else {
                    throw new AppException(AppError.DEPENDENCY_RESPONSE_NOT_VALID, joinPoint.getSignature().toShortString(), msg);
                }
            }
        }
    }

}
