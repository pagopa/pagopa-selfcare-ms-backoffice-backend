package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import feign.Response;
import it.pagopa.selfcare.pagopa.backoffice.client.*;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationTestDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestResultEnum;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.TestStationResource;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StationServiceTest {

    @Autowired
    private StationService service;

    @MockBean
    private WrapperService wrapperService;

    @MockBean
    private AwsSesClient awsSesClient;

    @MockBean
    private ForwarderTestClient forwarderTestClient;

    @MockBean
    private JiraServiceManagerClient jiraServiceManagerClient;

    @Test
    void testStationShouldReturnSuccessOnValidForwardCall() {
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.SUCCESS, testStationResource.getTestResult());
        verify(forwarderTestClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldSuccessOnBadRequestForwardCall() {
        when(forwarderTestClient.testForwardConnection(any(),any(),any())).thenAnswer(item -> {
           Response response = Response.builder().status(400).build();
           throw FeignException.errorStatus("test", response);
        });
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.SUCCESS, testStationResource.getTestResult());
        verify(forwarderTestClient).testForwardConnection(any(),any(),any());
    }

    @Test
    void testStationShouldReturnKOOnErrorForwardCall() {
        when(forwarderTestClient.testForwardConnection(any(),any(),any())).thenAnswer(item -> {
            Response response = Response.builder().status(500).build();
            throw FeignException.errorStatus("test", response);
        });
        TestStationResource testStationResource = assertDoesNotThrow(() ->
                service.testStation(StationTestDto.builder().build()));
        assertNotNull(testStationResource);
        assertEquals(TestResultEnum.ERROR, testStationResource.getTestResult());
        verify(forwarderTestClient).testForwardConnection(any(),any(),any());
    }

}