package it.pagopa.selfcare.pagopa.backoffice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.Problem;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtAuthenticationFilter;
import it.pagopa.selfcare.pagopa.backoffice.security.JwtAuthenticationProvider;
import it.pagopa.selfcare.pagopa.backoffice.security.PagopaAuthenticationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@Import(WebConfig.class)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/favicon.ico",
            "/error",
            "/actuator/**",
            "/channels/getBrokersPsp/**",
            "/stations/brokers-EC/**"
    };

    private final ObjectMapper objectMapper;
    private final PagopaAuthenticationStrategy jwtAuthenticationStrategyFactory;

    public SecurityConfig(ObjectMapper objectMapper, PagopaAuthenticationStrategy jwtAuthenticationStrategyFactory) {
        this.objectMapper = objectMapper;
        this.jwtAuthenticationStrategyFactory = jwtAuthenticationStrategyFactory;

    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtAuthenticationStrategyFactory);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.eraseCredentials(false);
    }


    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("{} to resource {}", accessDeniedException.getMessage(), request.getRequestURI());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    final Problem problem = new Problem(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
                    response.getOutputStream().print(objectMapper.writeValueAsString(problem));
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("{} {}", authException.getMessage(), request.getRequestURI());
                    response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"selfcare\"");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    final Problem problem = new Problem(HttpStatus.UNAUTHORIZED, authException.getMessage());
                    response.getOutputStream().print(objectMapper.writeValueAsString(problem));
                })
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .anonymous().disable()
                .rememberMe().disable()
                .x509().disable()
                .addFilterBefore(new JwtAuthenticationFilter(getApplicationContext().getBean(AuthenticationManager.class), objectMapper), UsernamePasswordAuthenticationFilter.class);
    }

}
