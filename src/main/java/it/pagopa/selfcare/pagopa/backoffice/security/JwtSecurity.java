package it.pagopa.selfcare.pagopa.backoffice.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JwtSecurity {

  String paramName() default "";
  boolean skipCheckIfParamIsNull() default false;
}
