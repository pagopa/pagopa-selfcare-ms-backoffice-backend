package it.pagopa.selfcare.pagopa.backoffice.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidityDateRequiredForCreateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidityDateRequiredForCreate {
    String message() default "validityDate is required when operation type is CREATE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}