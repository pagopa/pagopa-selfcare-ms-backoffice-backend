package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.exception.JwtAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    public static final String START_TIME = "startTime";
    public static final String METHOD = "method";
    public static final String STATUS = "status";
    public static final String CODE = "httpCode";
    public static final String RESPONSE_TIME = "responseTime";

    @Value("${info.application.name}")
    private String name;

    @Value("${info.application.version}")
    private String version;

    @Value("${info.properties.environment}")
    private String environment;

    @Autowired
    HttpServletRequest httRequest;

    @Autowired
    HttpServletResponse httpResponse;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {
        // all rest controllers
    }

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repository() {
        // all repository methods
    }

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void service() {
        // all service methods
    }

    /**
     * Log essential info of application during the startup.
     */
    @PostConstruct
    public void logStartup() {
        log.info("-> Starting {} version {} - environment {}", name, version, environment);
    }

    @Around(value = "restController()")
    public Object logApiInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        MDC.put(METHOD, joinPoint.getSignature().getName());
        MDC.put(START_TIME, String.valueOf(System.currentTimeMillis()));
        log.info("{} {}", httRequest.getMethod(), httRequest.getRequestURI());
        log.info("Invoking API operation {} - args: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());

        Object result = joinPoint.proceed();

        MDC.put(STATUS, "OK");
        MDC.put(CODE, String.valueOf(httpResponse.getStatus()));
        MDC.put(RESPONSE_TIME, getExecutionTime());
        log.info("Successful API operation {} - result: {}", joinPoint.getSignature().getName(), result);
        MDC.remove(STATUS);
        MDC.remove(CODE);
        MDC.remove(RESPONSE_TIME);
        MDC.remove(START_TIME);
        return result;
    }

    @AfterReturning(value = "execution(* *..web.handler..*(..))", returning = "result")
    public void trowingApiInvocation(JoinPoint joinPoint, ResponseEntity<?> result) {
        MDC.put(STATUS, "KO");
        MDC.put(CODE, String.valueOf(result.getStatusCodeValue()));
        MDC.put(RESPONSE_TIME, getExecutionTime());
        log.info("Failed API operation {} - error: {}", MDC.get(METHOD), result);
        MDC.remove(STATUS);
        MDC.remove(CODE);
        MDC.remove(RESPONSE_TIME);
        MDC.remove(START_TIME);
    }

    @AfterThrowing(pointcut = "execution(* *..web.security..*(..))", throwing = "error")
    public void afterThrowingAdvice(JoinPoint jp, JwtAuthenticationException error){
        MDC.put(STATUS, "KO");
        MDC.put(CODE, "401");
        MDC.put(RESPONSE_TIME, getExecutionTime());
        String method = httRequest.getMethod() + httRequest.getRequestURI();
        MDC.put(METHOD, method);
        log.info("{} {}", httRequest.getMethod(), httRequest.getRequestURI());
        log.info("Failed API operation {} - error: {}", method, error.getMessage());
        MDC.remove(STATUS);
        MDC.remove(CODE);
        MDC.remove(RESPONSE_TIME);
        MDC.remove(START_TIME);
    }

    @Around(value = "repository() || service()")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Call method {} - args: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        Object result = joinPoint.proceed();
        log.debug("Return method {} - result: {}", joinPoint.getSignature().toShortString(), result);
        return result;
    }

    private static String getExecutionTime() {
        long endTime = System.currentTimeMillis();
        long startTime = Long.parseLong(deNull(MDC.get(START_TIME)));
        long executionTime = endTime - startTime;
        return String.valueOf(executionTime);
    }

    private static String deNull(String s) {
        return s == null ? String.valueOf(System.currentTimeMillis()) : s;
    }
}
