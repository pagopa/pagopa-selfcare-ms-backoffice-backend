package it.pagopa.selfcare.pagopa.backoffice.config;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.util.JwtSecurity;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class JwtAspect {

  @Autowired FeatureManager featureManager;

  @Autowired HttpServletRequest httRequest;

  @Autowired HttpServletResponse httpResponse;

  @Pointcut("@annotation(it.pagopa.selfcare.pagopa.backoffice.util.JwtSecurity)")
  public void jwtSecurity() {
    // all repository methods
  }

  @Around(value = "jwtSecurity()")
  public Object checkJwt(ProceedingJoinPoint joinPoint) throws Throwable {
    Method method = getMethodFromJoinPoint(joinPoint);
    JwtSecurity annotation = method.getAnnotation(JwtSecurity.class);

    var paramValue = getParamValue(joinPoint);

    if (Boolean.FALSE.equals(featureManager.isEnabled("operator"))) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String taxCode = Utility.extractOrgVatFromAuth(authentication);

      if (paramValue!=null && !paramValue.equals(taxCode)) {
        throw new AppException(AppError.UNAUTHORIZED);
      }
    }

    Object result = joinPoint.proceed();
    return result;
  }

  private static String getParamValue(ProceedingJoinPoint joinPoint) {
    // Ottieni i parametri
    Object[] args = joinPoint.getArgs();
    String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

    // Cerca brokerCode tra i parametri
    for (int i = 0; i < paramNames.length; i++) {
      if ("brokerCode".equals(paramNames[i])) {

        return args[i] != null ? args[i].toString() : null;
      }
    }
    return null;
  }

  private Method getMethodFromJoinPoint(ProceedingJoinPoint joinPoint)
      throws NoSuchMethodException {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // In case of proxies, get the actual implementation method
    if (method.getDeclaringClass().isInterface()) {
      method =
          joinPoint
              .getTarget()
              .getClass()
              .getDeclaredMethod(signature.getName(), signature.getParameterTypes());
    }

    return method;
  }
}
