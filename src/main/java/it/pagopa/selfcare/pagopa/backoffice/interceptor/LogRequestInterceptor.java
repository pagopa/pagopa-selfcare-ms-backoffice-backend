package it.pagopa.selfcare.pagopa.backoffice.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.HEADER_REQUEST_ID;

@Slf4j
@Component
public class LogRequestInterceptor implements HandlerInterceptor {

    private static final Collection<String> URI_PREFIX_WHITELIST = List.of(
            "/swagger",
            "/v3/api-docs"
    );


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object controller) {
        HttpServletRequest httRequest = request;

        // get requestId from header or generate one
        String requestId = httRequest.getHeader(HEADER_REQUEST_ID);
        if(requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        // set requestId in MDC
        MDC.put("requestId", requestId);
        boolean skipLog = URI_PREFIX_WHITELIST.stream()
                .anyMatch(request.getRequestURI()::startsWith);
        if(!skipLog) {

            String query = "";
            if(!request.getParameterMap().isEmpty()) {
                query = "?"+request.getParameterMap().entrySet()
                        .stream()
                        .map(elem -> elem.getKey() + "=" + String.join("", elem.getValue()))
                        .collect(Collectors.joining("&"));
            }
            log.info("Requested {} {}{}", request.getMethod(), request.getRequestURI(), query);
        }
        response.setHeader(HEADER_REQUEST_ID, requestId);
        return true;
    }

}
