package it.pagopa.selfcare.pagopa.backoffice.security;

import it.pagopa.selfcare.pagopa.backoffice.model.institutions.ProductRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define security constraints
 * on controller methods that require JWT (JSON Web Token) validation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JwtSecurity {

  /**
   * The name of the parameter that holds the value against which the security check will be made.
   *
   * @return the parameter name
   */
  String paramName() default ""; // Default is an empty string if not specified

  /**
   * Flag to determine whether to skip the security check if the value of
   * the parameter specified in {@link JwtSecurity#paramName()} is null.
   *
   * @return true if the security check should be skipped, false otherwise
   */
  boolean skipCheckIfParamIsNull() default false;

  /**
   * Flag to determine if the suffix of the parameter value should be removed.
   * This is used to extract the tax code from the station and channel codes.
   *
   * @return true if the suffix should be removed, false otherwise
   */
  boolean removeParamSuffix() default false;

  /**
   * Indicates whether to search for the parameter specified in {@link JwtSecurity#paramName()} inside the request body.
   *
   * @return true if the parameter should be searched in the body, false otherwise
   */
  boolean checkParamInsideBody() default false;

  /**
   * Flag to specify if the parameter value should be treated as a user ID.
   * The default is to check it as a tax code.
   *
   * @return true if the parameter value is treated as a user ID, false otherwise
   */
  boolean checkParamAsUserId() default false;

  /**
   * Specifies the allowed product role for the method.
   * This can restrict access based on the user's role.
   *
   * @return the allowed ProductRole
   */
  ProductRole allowedProductRole() default ProductRole.ALL;

  /**
   * The name of a fallback parameter that can be used
   * if the value of the parameter specified in {@link JwtSecurity#paramName()} is null.
   *
   * @return the name of the fallback parameter
   */
  String fallbackParamName() default "";
}
