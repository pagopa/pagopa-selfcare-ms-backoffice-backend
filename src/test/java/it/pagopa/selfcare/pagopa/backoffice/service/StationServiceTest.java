package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ForwarderClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestResultEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import kong.unirest.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {StationService.class})
class StationServiceTest {

    @Autowired
    private StationService service;

    @MockBean
    private WrapperService wrapperService;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private AwsSesClient awsSesClient;

    @MockBean
    private ForwarderClient forwarderClient;

    @MockBean
    private JiraServiceManagerClient jiraServiceManagerClient;

    @Test
    void testStationShouldReturnSuccessOnValidForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(200);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.SUCCESS, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldSuccessOnBadRequestForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(400);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldReturnSuccessOnErrorForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(500);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }
    @Test
    void testStationShouldReturnKOOnErrorForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(500);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldReturnCertErrorOnCertErrorForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(401);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.CERTIFICATE_ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldReturnErrorOnNotFoundForwardCall() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.getStatus()).thenReturn(404);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenReturn(response);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

}
