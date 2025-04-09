//package it.pagopa.selfcare.pagopa.backoffice.config;
//
//import static it.pagopa.selfcare.pagopa.backoffice.config.LoggingAspect.REQUEST_ID;
//import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.HEADER_REQUEST_ID;
//
//import com.azure.spring.cloud.feature.management.FeatureManager;
//import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
//import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
//import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
//import java.io.IOException;
//import java.util.UUID;
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.constraints.Size;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.catalina.connector.ClientAbortException;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class JwtFilter implements Filter {
//
//  @Autowired FeatureManager featureManager;
//
//  /**
//   * Get the request ID from the custom header "X-Request-Id" if present, otherwise it generates
//   * one. Set the X-Request-Id value in the {@code response} and in the MDC
//   *
//   * @param request http request
//   * @param response http response
//   * @param chain next filter
//   * @throws IOException if an I/O error occurs during this filter's processing of the request
//   * @throws ServletException if the processing fails for any other reason
//   */
//  @Override
//  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//      throws IOException, ServletException {
//      HttpServletRequest httRequest = (HttpServletRequest) request;
//
//      // get requestId from header or generate one
//      String requestId = httRequest.getHeader(HEADER_REQUEST_ID);
//      if (requestId == null || requestId.isEmpty()) {
//        requestId = UUID.randomUUID().toString();
//      }
//
//      // set requestId in MDC
//      MDC.put(REQUEST_ID, requestId);
//
//      log.info("{} {}", httRequest.getMethod(), httRequest.getRequestURI());
//
//      if (Boolean.FALSE.equals(featureManager.isEnabled("operator"))) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String taxCode = Utility.extractOrgVatFromAuth(authentication);
//
//        if (!httRequest.getRequestURI().contains(taxCode) &&
//                !(httRequest.getQueryString() != null && httRequest.getQueryString().contains(taxCode))) {
//          throw new AppException(AppError.UNAUTHORIZED);
//        }
//      }
//
//      // set requestId in the response header
//      ((HttpServletResponse) response).setHeader(HEADER_REQUEST_ID, requestId);
//      chain.doFilter(request, response);
//
//  }
//}
