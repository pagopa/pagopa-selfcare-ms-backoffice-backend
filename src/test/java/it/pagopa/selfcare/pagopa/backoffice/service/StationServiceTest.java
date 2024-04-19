package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import feign.Request;
import feign.Response;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ForwarderClient;
import it.pagopa.selfcare.pagopa.backoffice.client.JiraServiceManagerClient;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestResultEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.SUCCESS, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldSuccessOnBadRequestForwardCall() {
        Response response = Response.builder().status(400).request(
                Request.create(Request.HttpMethod.POST, "test",
                        new HashMap<>(), "".getBytes(), null, null)).build();
        FeignException feignException = FeignException.errorStatus("test", response);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenThrow(feignException);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldReturnSuccessOnErrorForwardCall() {
        Response response = Response.builder().status(500).request(
                Request.create(Request.HttpMethod.POST, "test", new HashMap<>(),
                        "".getBytes(), null, null)).build();
        FeignException feignException = FeignException.errorStatus("test", response);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenThrow(feignException);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }
    @Test
    void testStationShouldReturnKOOnErrorForwardCall() {
        Response response = Response.builder().status(500).request(
                Request.create(Request.HttpMethod.POST, "test",
                        new HashMap<>(), "".getBytes(), null, null)).build();
        FeignException feignException = FeignException.errorStatus("test", response);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenThrow(feignException);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldReturnCertErrorOnCertErrorForwardCall() {
        Response response = Response.builder().status(401).request(
                Request.create(Request.HttpMethod.POST, "test", new HashMap<>(),
                        "".getBytes(), null, null)).build();
        FeignException feignException = FeignException.errorStatus("test", response);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenThrow(feignException);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.CERTIFICATE_ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldReturnErrorOnNotFoundForwardCall() {
        Response response = Response.builder().status(404).request(
                Request.create(Request.HttpMethod.POST, "test", new HashMap<>(),
                        "".getBytes(), null, null)).build();
        FeignException feignException = FeignException.errorStatus("test", response);
        when(forwarderClient.testForwardConnection(any(),any(),any())).thenThrow(feignException);
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderClient).testForwardConnection(any(),any(),any());
    }

}
