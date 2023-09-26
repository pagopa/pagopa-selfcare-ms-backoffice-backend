package it.pagopa.selfcare.pagopa.backoffice.config;

import it.pagopa.selfcare.pagopa.backoffice.core.Secret;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Value("${info.build.artifact}")
    private String artifactId;

    @Value("${info.build.version}")
    private String version;

    @Value("${info.env}")
    private String environment;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {
        // all rest controllers
    }

    @Pointcut("execution(* it.pagopa.selfcare.pagopa.backoffice..*(..)) && (@within(org.springframework.stereotype.Service) || within(@org.springframework.stereotype.Repository *))")
    public void service() {
        // all service methods
    }

    @Pointcut("@within(org.springframework.cloud.openfeign.FeignClient)")
    public void feignClient() {
        // all feignClient methods
    }

    @Pointcut("execution(* it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler.*(..))")
    public void errorHandler() {
        // all service methods
    }

    /**
     * Log essential info of application during the startup.
     */
    @PostConstruct
    public void logStartup() {
        log.info("-> Starting {} version {} - environment {}", artifactId, version, environment);
    }

    /**
     * If DEBUG log-level is enabled prints the env variables and the application properties.
     *
     * @param event Context of application
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        log.debug("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(sources.spliterator(), false)
                .filter(EnumerablePropertySource.class::isInstance)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(prop -> !(prop.toLowerCase().contains("credentials") || prop.toLowerCase()
                        .contains("password") || prop.toLowerCase().contains("pass") || prop.toLowerCase()
                        .contains("pwd") || prop.toLowerCase()
                        .contains("key") || prop.toLowerCase()
                        .contains("secret")))
                .forEach(prop -> log.debug("{}: {}", prop, env.getProperty(prop)));
    }

    @Before(value = "restController()")
    public void logApiInvocation(JoinPoint joinPoint) {
        log.info("Invoking API operation {} - args: {}", joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @AfterReturning(value = "restController()", returning = "result")
    public void returnApiInvocation(JoinPoint joinPoint, Object result) {
        log.info("Successful API operation {} - result: {}", joinPoint.getSignature().getName(),
                result);
    }

    @AfterReturning(value = "errorHandler()", returning = "result")
    public void trowingApiInvocation(JoinPoint joinPoint, Object result) {
        log.info("Failed API operation {} - error: {}", joinPoint.getSignature().getName(), result);
    }

    @Around(value = "service()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.trace("Time taken for Execution of {} is: {}ms", joinPoint.getSignature().toShortString(),
                (endTime - startTime));
        return result;
    }

    @Around(value = "service() || feignClient()")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        var args = IntStream.range(0, joinPoint.getArgs().length)
                .filter(i -> i % 2 == 0)
                .mapToObj(i -> {
                    var arg = joinPoint.getArgs()[i] == null ? "null" : joinPoint.getArgs()[i].toString();
                    var isSecret = signature.getMethod().getParameters()[i].getAnnotation(Secret.class);
                    return isSecret != null ? "***" : arg;
                })
                .map(elem -> elem.replace(" (\\w){32,}", "***"))
                .map(elem -> elem.replace("\\n", " "))
                .collect(Collectors.toList());

        log.debug("Start Method {} - args: {}", joinPoint.getSignature().toShortString(),
                args);
        Object result = joinPoint.proceed();
        log.debug("Return Method {} - result: {}", joinPoint.getSignature().toShortString(), result);
        return result;
    }

}
