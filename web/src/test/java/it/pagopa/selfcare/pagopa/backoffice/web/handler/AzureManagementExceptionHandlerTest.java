package it.pagopa.selfcare.pagopa.backoffice.web.handler;

import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.exception.ManagementException;
import it.pagopa.selfcare.pagopa.backoffice.web.model.Problem;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class AzureManagementExceptionHandlerTest {
    private static final String DETAIL_MESSAGE = "detail message";
    private final AzureManagementExceptionHandler handler;

    AzureManagementExceptionHandlerTest() {
        this.handler = new AzureManagementExceptionHandler();
    }
    
    @Test
    void handleManagementException_resourceNotFound(){
        //given
        HttpResponseException exception = new ManagementException(DETAIL_MESSAGE, new MockHttpResponse(null, 404));
        //when
        ResponseEntity<Problem> responseEntity = handler.handleManagementException(exception);
        //then
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
    }
    
    @Test
    void handleManagementException_differentResponse(){
        //given
        HttpResponseException exception = new ManagementException(DETAIL_MESSAGE, new MockHttpResponse(null, 400));
        //when
        ResponseEntity<Problem> responseEntity = handler.handleManagementException(exception);
        //then
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
    }
    
    
    
    private static class MockHttpResponse extends HttpResponse{
        private int statusCode;
        /**
         * Creates an instance of {@link HttpResponse}.
         *
         * @param request The {@link HttpRequest} that resulted in this {@link HttpResponse}.
         */
        protected MockHttpResponse(HttpRequest request, int statusCode) {
            super(request);
            this.statusCode =statusCode;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String getHeaderValue(String name) {
            return null;
        }

        @Override
        public HttpHeaders getHeaders() {
            return null;
        }

        @Override
        public Flux<ByteBuffer> getBody() {
            return null;
        }

        @Override
        public Mono<byte[]> getBodyAsByteArray() {
            return null;
        }

        @Override
        public Mono<String> getBodyAsString() {
            return null;
        }

        @Override
        public Mono<String> getBodyAsString(Charset charset) {
            return null;
        }
    }
}
