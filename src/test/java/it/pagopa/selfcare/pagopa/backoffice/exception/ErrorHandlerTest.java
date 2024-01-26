package it.pagopa.selfcare.pagopa.backoffice.exception;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    @Test
    void handleFeignException() {
        FeignException exception = Mockito.spy(new FeignException.BadRequest("", Mockito.mock(Request.class), null, null));
        WebRequest request = Mockito.spy(WebRequest.class);
        var response = new ErrorHandler().handleFeignException(exception, request);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    }
}
