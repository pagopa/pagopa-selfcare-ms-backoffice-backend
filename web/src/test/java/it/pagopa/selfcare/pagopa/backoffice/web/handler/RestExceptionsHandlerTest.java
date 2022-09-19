package it.pagopa.selfcare.pagopa.backoffice.web.handler;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.web.model.Problem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.ServletException;
import javax.validation.ValidationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.*;

class RestExceptionsHandlerTest {
    private static final String DETAIL_MESSAGE = "detail message";

    private final RestExceptionsHandler handler;


    public RestExceptionsHandlerTest() {
        this.handler = new RestExceptionsHandler();
    }


    @Test
    void handleThrowable() {
        // given
        Throwable exceptionMock = Mockito.mock(Throwable.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ResponseEntity<Problem> responseEntity = handler.handleThrowable(exceptionMock);
        // then
        assertNotNull(responseEntity);
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(INTERNAL_SERVER_ERROR.value(), responseEntity.getBody().getStatus());
    }


    @ParameterizedTest
    @ValueSource(classes = {
            ValidationException.class,
            BindException.class,
            ServletException.class,
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class,
            HttpMessageNotReadableException.class
    })
    void handleBadRequestException(Class<?> clazz) {
        // given
        Exception exceptionMock = (Exception) Mockito.mock(clazz);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ResponseEntity<Problem> responseEntity = handler.handleBadRequestException(exceptionMock);
        // then
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(BAD_REQUEST.value(), responseEntity.getBody().getStatus());
    }


    @Test
    void handleHttpMediaTypeNotAcceptableException() {
        // given
        HttpMediaTypeNotAcceptableException exceptionMock = Mockito.mock(HttpMediaTypeNotAcceptableException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ResponseEntity<Problem> responseEntity = handler.handleHttpMediaTypeNotAcceptableException(exceptionMock);
        // then
        assertNotNull(responseEntity);
        assertEquals(NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(NOT_ACCEPTABLE.value(), responseEntity.getBody().getStatus());
    }


    @Test
    void handleHttpRequestMethodNotSupportedException() {
        // given
        HttpRequestMethodNotSupportedException exceptionMock = Mockito.mock(HttpRequestMethodNotSupportedException.class);
        Mockito.when(exceptionMock.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ResponseEntity<Problem> responseEntity = handler.handleHttpRequestMethodNotSupportedException(exceptionMock);
        // then
        assertNotNull(responseEntity);
        assertEquals(METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(DETAIL_MESSAGE, responseEntity.getBody().getDetail());
        assertEquals(METHOD_NOT_ALLOWED.value(), responseEntity.getBody().getStatus());
    }


    @Test
    void handleMethodArgumentNotValidException() {
        // given
        MethodArgumentNotValidException exceptionMock = Mockito.mock(MethodArgumentNotValidException.class);
        final FieldError errorMock = new FieldError("objectName", "fieldName", "is not valid");
        Mockito.when(exceptionMock.getFieldErrors())
                .thenReturn(List.of(errorMock));
        // when
        ResponseEntity<Problem> responseEntity = handler.handleMethodArgumentNotValidException(exceptionMock);
        // then
        assertNotNull(responseEntity);
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Validation failed", responseEntity.getBody().getDetail());
        assertNotNull(responseEntity.getBody().getInvalidParams());
        assertEquals(1, responseEntity.getBody().getInvalidParams().size());
        assertEquals(errorMock.getObjectName() + "." + errorMock.getField(), responseEntity.getBody().getInvalidParams().get(0).getName());
        assertEquals(errorMock.getDefaultMessage(), responseEntity.getBody().getInvalidParams().get(0).getReason());
        assertEquals(BAD_REQUEST.value(), responseEntity.getBody().getStatus());
    }


    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"BAD_REQUEST", "INTERNAL_SERVER_ERROR", "OK"})
    void handleFeignException(HttpStatus httpStatus) {
        // given
        FeignException exceptionMock = Mockito.mock(FeignException.class);
        Mockito.when(exceptionMock.status())
                .thenReturn(httpStatus.value());
        // when
        ResponseEntity<Problem> responseEntity = handler.handleFeignException(exceptionMock);
        // then
        assertNotNull(responseEntity);
        assertEquals(httpStatus.is2xxSuccessful() ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("An error occurred during a downstream service request", responseEntity.getBody().getDetail());
    }
}
