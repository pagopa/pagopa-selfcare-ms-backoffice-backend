package it.pagopa.selfcare.pagopa.backoffice.config;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.util.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class JwtAspect {

  private final FeatureManager featureManager;

  @Autowired
  public JwtAspect(FeatureManager featureManager) {
    this.featureManager = featureManager;
  }

  @Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(jwtSecurity)")
  public void checkJwt(final JoinPoint joinPoint, final JwtSecurity jwtSecurity) throws Throwable {
    var paramValue = getParamValue(joinPoint, jwtSecurity.paramName());

    if (!Boolean.TRUE.equals(featureManager.isEnabled("operator"))) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String taxCode = Utility.extractOrgVatFromAuth(authentication);

      if (paramValue == null || !paramValue.equals(taxCode)) {
        throw new AppException(AppError.UNAUTHORIZED);
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
