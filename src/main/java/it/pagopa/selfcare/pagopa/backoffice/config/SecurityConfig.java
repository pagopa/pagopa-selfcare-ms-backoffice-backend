package it.pagopa.selfcare.pagopa.backoffice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.model.ProblemJson;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtAuthenticationFilter;
import it.pagopa.selfcare.pagopa.backoffice.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.*;

@Slf4j
@Configuration
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/favicon.ico",
            "/error",
            "/actuator/**",
            "/info"
    };

    @Value("${info.properties.environment}")
    private String env;

    @Value("${jwt.enabled}")
    private Boolean jwtEnabled;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // skip jwt-check during junit test
        // the user in the security context is mocked
        if("test".equals(env) || Boolean.TRUE.equals(jwtEnabled)) {
            return web -> web.ignoring().antMatchers("/**");
        } else {
            return web -> web.ignoring().antMatchers(AUTH_WHITELIST);
        }
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    ProblemJson problem = new ProblemJson(AppError.FORBIDDEN.getTitle(), HttpStatus.FORBIDDEN.value(), AppError.FORBIDDEN.getDetails());
                    setMDC(problem);
                    log.info("{} to resource {}", accessDeniedException.getMessage(), request.getRequestURI());
                    response.getOutputStream().print(objectMapper.writeValueAsString(problem));
                    MDC.clear();
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"selfcare\"");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    ProblemJson problem = new ProblemJson(AppError.UNAUTHORIZED.getDetails(), HttpStatus.UNAUTHORIZED.value(), AppError.UNAUTHORIZED.getDetails());
                    setMDC(problem);
                    log.info("{} {}", authException.getMessage(), request.getRequestURI());
                    response.getOutputStream().print(objectMapper.writeValueAsString(problem));
                    MDC.clear();
                })
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    return corsConfiguration;
                })
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .anonymous().disable()
                .rememberMe().disable()
                .x509().disable()
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void setMDC(ProblemJson problem) {
        MDC.put(STATUS, "KO");
        MDC.put(CODE, String.valueOf(problem.getStatus()));
        MDC.put(RESPONSE_TIME, getExecutionTime());
        MDC.put(METHOD, "filterChain");
        MDC.put(FAULT_CODE, problem.getTitle());
        MDC.put(FAULT_DETAIL, problem.getDetail());
    }


}
