package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationTypeEnum;
import kong.unirest.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForwarderClientTest {

    @Mock
    private UnirestInstance unirestInstance;

    private ForwarderClient forwarderClient;

    @BeforeEach
    public void init() {
        GetRequest getRequest = Mockito.mock(GetRequest.class);
        HttpRequestWithBody httpRequestWithBody = Mockito.mock(HttpRequestWithBody.class);
        HttpResponse basicResponse = Mockito.mock(HttpResponse.class);
        RequestBodyEntity requestBodyEntity = Mockito.mock(RequestBodyEntity.class);
        Mockito.reset(unirestInstance);
        lenient().when(unirestInstance.get(any())).thenReturn(getRequest);
        lenient().when(unirestInstance.post(any())).thenReturn(httpRequestWithBody);
        lenient().when(getRequest.asString()).thenReturn(basicResponse);
        lenient().when(httpRequestWithBody.header(any(),any())).thenReturn(httpRequestWithBody);
        lenient().when(httpRequestWithBody.body(anyString())).thenReturn(requestBodyEntity);
        lenient().when(requestBodyEntity.asString()).thenReturn(basicResponse);
        lenient().when(basicResponse.getStatus()).thenReturn(200);
        forwarderClient = new ForwarderClient();
        forwarderClient.setUnirest(unirestInstance);
    }

    @Test
    public void shouldReturnOkONPaVerify() {
        HttpResponse<String> testResponse = forwarderClient.testForwardConnection("https","test"
                ,443, "/", TestStationTypeEnum.PA_VERIFY);
        assertNotNull(testResponse);
        assertEquals(200, testResponse.getStatus());
        verify(unirestInstance).post(any());
    }

    @Test
    public void shouldReturnOkONInviaRT() {
        HttpResponse<String> testResponse = forwarderClient.testForwardConnection("https","test"
                ,443, "/", TestStationTypeEnum.PA_INVIA_RT);
        assertNotNull(testResponse);
        assertEquals(200, testResponse.getStatus());
        verify(unirestInstance).post(any());
    }

    @Test
    public void shouldReturnOkONGet() {
        HttpResponse<String> testResponse = forwarderClient.testForwardConnection("https","test"
                ,443, "/", TestStationTypeEnum.PA_REDIRECT);
        assertNotNull(testResponse);
        assertEquals(200, testResponse.getStatus());
        verify(unirestInstance).get(any());
    }

}