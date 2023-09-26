package it.pagopa.selfcare.pagopa.backoffice.connector.rest.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

  private static final String HEADER_REQUEST_ID = "X-Request-Id";

  @Bean
  public RequestInterceptor requestIdInterceptor() {
    return requestTemplate -> requestTemplate.header(HEADER_REQUEST_ID, MDC.get("requestId"));
  }

}
