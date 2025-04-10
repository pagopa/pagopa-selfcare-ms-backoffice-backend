package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class JwtAspect {

  private static final String LOCAL_ENV = "local";
  private static final String TEST_ENV = "test";

  private final String environment;

  @Autowired
  public JwtAspect(@Value("${info.properties.environment}") String environment) {
    this.environment = environment;
  }

  @Before(
      "within(@org.springframework.web.bind.annotation.RestController *) && @annotation(jwtSecurity)")
  public void checkJwt(final JoinPoint joinPoint, final JwtSecurity jwtSecurity) {
    var paramValue = getParamValue(joinPoint, jwtSecurity.paramName());

    if (!this.environment.equals(LOCAL_ENV) && !this.environment.equals(TEST_ENV)) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String institutionTaxCode = Utility.extractInstitutionTaxCodeFromAuth(authentication);

      if (paramValue == null || !paramValue.equals(institutionTaxCode)) {
        throw new AppException(AppError.FORBIDDEN);
      }
    }
  }

  private static String getParamValue(JoinPoint joinPoint, String requestedParam) {
    // retrieve parameters
    Object[] args = joinPoint.getArgs();
    String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

    // find requested param
    for (int i = 0; i < paramNames.length; i++) {
      if (requestedParam.equals(paramNames[i])) {
        return args[i] != null ? args[i].toString() : null;
      }
    }
    return null;
  }
}
