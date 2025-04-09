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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class JwtAspect {

  private final String environment;
  private final FeatureManager featureManager;

  @Autowired
  public JwtAspect(@Value("${info.properties.environment}") String environment, FeatureManager featureManager) {
      this.environment = environment;
      this.featureManager = featureManager;
  }

  @Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(jwtSecurity)")
  public void checkJwt(final JoinPoint joinPoint, final JwtSecurity jwtSecurity) {
    var paramValue = getParamValue(joinPoint, jwtSecurity.paramName());

    if (!this.environment.equals("local") && !Boolean.TRUE.equals(featureManager.isEnabled("operator"))) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String taxCode = Utility.extractInstitutionTaxCodeFromAuth(authentication);

      if (paramValue != null && jwtSecurity.removeParamSuffix()) {
        paramValue = paramValue.split("_")[0];
      }
      if ((paramValue == null && !jwtSecurity.skipCheckIfParamIsNull())
          || (paramValue != null && !paramValue.equals(taxCode))) {
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
