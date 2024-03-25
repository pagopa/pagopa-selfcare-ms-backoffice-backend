package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.MyCIResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokersResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsResourceList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MappingsConfiguration.class, BrokerService.class})
class BrokerServiceTest {

    private static final String CBILL_1 = "cbill1";
    private static final String CBILL_2 = "cbill2";
    private static final String BROKER_CODE = "brokerCode";
    private static final String BROKER_ID = "brokerId";
    private static final String INSTITUTION_TAX_CODE_1 = "institutionTaxCode1";
    private static final String INSTITUTION_TAX_CODE_2 = "institutionTaxCode2";
    private static final String INSTITUTION_TAX_CODE_3 = "institutionTaxCode3";

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @MockBean
    private ExternalApiClient externalApiClient;

    @Autowired
    private BrokerService sut;

    @Test
    void createBrokerTest() {
        when(apiConfigClient.createBroker(any())).thenReturn(new BrokerDetails());

        BrokerResource result = assertDoesNotThrow(() -> sut.createBroker(any()));

        assertNotNull(result);
    }

    @Test
    void updateBrokerForCITest() {
        when(apiConfigClient.updateBrokerEc(any(), anyString()))
                .thenReturn(new BrokerDetails());

        BrokerDetailsResource result =
                assertDoesNotThrow(() -> sut.updateBrokerForCI(any(), anyString()));

        assertNotNull(result);
    }

    @Test
    void getStationsDetailsListByBrokerTest() {
        when(apiConfigSelfcareIntegrationClient
                .getStationsDetailsListByBroker(anyString(), anyString(), eq(null), anyInt(), anyInt()))
                .thenReturn(new StationDetailsList());

        StationDetailsResourceList result = assertDoesNotThrow(() ->
                sut.getStationsDetailsListByBroker("brokercode", "stationId", 1, 0));

        assertNotNull(result);
    }

    @Test
    void getBrokersECTest() {
        when(apiConfigClient.getBrokersEC(anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(new Brokers());

        BrokersResource result = assertDoesNotThrow(() -> sut.getBrokersEC(anyInt(),
                anyInt(), anyString(), anyString(), anyString(), anyString()));

        assertNotNull(result);
    }

    @Test
    void getBrokerDelegationTest() {
        List<DelegationExternal> delegationExternalList = new ArrayList<>();
        delegationExternalList.add(buildDelegationExternal("PA", INSTITUTION_TAX_CODE_1));
        delegationExternalList.add(buildDelegationExternal("GSP", INSTITUTION_TAX_CODE_2));
        delegationExternalList.add(buildDelegationExternal("PG", INSTITUTION_TAX_CODE_3));
        delegationExternalList.add(buildDelegationExternal("PSP", "institutionTaxCode4"));

        when(externalApiClient.getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString()))
                .thenReturn(delegationExternalList);

        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_1), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList());
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_2), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList());
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_3), anyInt(), anyInt()))
                .thenReturn(new StationDetailsList());

        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_1))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_1));
        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_2))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_2));

        List<MyCIResource> result = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, null, 0, 5));

        assertNotNull(result);
        assertNotEquals(delegationExternalList.size(), result.size());
        assertEquals(3, result.size());

        Optional<MyCIResource> first = result.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_1))
                .findFirst();
        assertTrue(first.isPresent());
        MyCIResource myCIResource1 = first.get();
        assertEquals(3L, myCIResource1.getInstitutionStationCount());
        assertEquals(CBILL_1, myCIResource1.getCbillCode());

        Optional<MyCIResource> second = result.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_2))
                .findFirst();
        assertTrue(second.isPresent());
        MyCIResource myCIResource2 = second.get();
        assertEquals(3L, myCIResource2.getInstitutionStationCount());
        assertEquals(CBILL_2, myCIResource2.getCbillCode());

        Optional<MyCIResource> third = result.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_3))
                .findFirst();
        assertTrue(third.isPresent());
        MyCIResource myCIResource3 = third.get();
        assertEquals(0L, myCIResource3.getInstitutionStationCount());
        assertNull(myCIResource3.getCbillCode());

        verify(externalApiClient).getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString());
        verify(apiConfigSelfcareIntegrationClient, times(3))
                .getStationsDetailsListByBroker(anyString(), eq(null), anyString(), anyInt(), anyInt());
        verify(apiConfigClient, times(3)).getCreditorInstitutionDetails(anyString());
    }

    @Test
    void getBrokerDelegationTestWithFilter() {
        List<DelegationExternal> delegationExternalList = new ArrayList<>();
        delegationExternalList.add(buildDelegationExternal("PA", INSTITUTION_TAX_CODE_1, "my test"));
        delegationExternalList.add(buildDelegationExternal("GSP", INSTITUTION_TAX_CODE_2, "test test"));
        delegationExternalList.add(buildDelegationExternal("PG", INSTITUTION_TAX_CODE_3, "pippo"));
        delegationExternalList.add(buildDelegationExternal("PSP", "institutionTaxCode4"));

        when(externalApiClient.getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString()))
                .thenReturn(delegationExternalList);

        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_1), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList());
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_2), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList());

        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_1))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_1));
        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_2))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_2));

        List<MyCIResource> result = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, "test", 0, 5));

        assertNotNull(result);
        assertNotEquals(delegationExternalList.size(), result.size());
        assertEquals(2, result.size());

        Optional<MyCIResource> first = result.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_1))
                .findFirst();
        assertTrue(first.isPresent());
        MyCIResource myCIResource1 = first.get();
        assertEquals(3L, myCIResource1.getInstitutionStationCount());
        assertEquals(CBILL_1, myCIResource1.getCbillCode());

        Optional<MyCIResource> second = result.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_2))
                .findFirst();
        assertTrue(second.isPresent());
        MyCIResource myCIResource2 = second.get();
        assertEquals(3L, myCIResource2.getInstitutionStationCount());
        assertEquals(CBILL_2, myCIResource2.getCbillCode());

        verify(externalApiClient).getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString());
        verify(apiConfigSelfcareIntegrationClient, times(2))
                .getStationsDetailsListByBroker(anyString(), eq(null), anyString(), anyInt(), anyInt());
        verify(apiConfigClient, times(2)).getCreditorInstitutionDetails(anyString());
    }

    @Test
    void getBrokerDelegationTestWithPagination() {
        List<DelegationExternal> delegationExternalList = new ArrayList<>();
        delegationExternalList.add(buildDelegationExternal("PA", INSTITUTION_TAX_CODE_1, "my test"));
        delegationExternalList.add(buildDelegationExternal("GSP", INSTITUTION_TAX_CODE_2, "test test"));
        delegationExternalList.add(buildDelegationExternal("PG", INSTITUTION_TAX_CODE_3, "pippo"));
        delegationExternalList.add(buildDelegationExternal("PSP", "institutionTaxCode4"));

        when(externalApiClient.getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString()))
                .thenReturn(delegationExternalList, delegationExternalList);

        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_1), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList());
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_2), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList());

        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_1))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_1));
        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_2))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_2));

        List<MyCIResource> firstPage = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, "test", 0, 5));
        List<MyCIResource> secondPage = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, "test", 1, 5));

        assertNotNull(firstPage);
        assertNotEquals(delegationExternalList.size(), firstPage.size());
        assertEquals(2, firstPage.size());

        assertNotNull(secondPage);
        assertEquals(0, secondPage.size());

        Optional<MyCIResource> first = firstPage.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_1))
                .findFirst();
        assertTrue(first.isPresent());
        MyCIResource myCIResource1 = first.get();
        assertEquals(3L, myCIResource1.getInstitutionStationCount());
        assertEquals(CBILL_1, myCIResource1.getCbillCode());

        Optional<MyCIResource> second = firstPage.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_2))
                .findFirst();
        assertTrue(second.isPresent());
        MyCIResource myCIResource2 = second.get();
        assertEquals(3L, myCIResource2.getInstitutionStationCount());
        assertEquals(CBILL_2, myCIResource2.getCbillCode());

        verify(externalApiClient, times(2))
                .getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString());
        verify(apiConfigSelfcareIntegrationClient, times(2))
                .getStationsDetailsListByBroker(anyString(), eq(null), anyString(), anyInt(), anyInt());
        verify(apiConfigClient, times(2)).getCreditorInstitutionDetails(anyString());
    }

    private static StationDetailsList buildStationDetailsList() {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalItems(3L);
        StationDetailsList stationDetailsList = new StationDetailsList();
        stationDetailsList.setPageInfo(pageInfo);
        return stationDetailsList;
    }

    private static CreditorInstitutionDetails buildCreditorInstitutionDetails(String cbill) {
        CreditorInstitutionDetails creditorInstitutionDetails =
                new CreditorInstitutionDetails();
        creditorInstitutionDetails.setCbillCode(cbill);
        return creditorInstitutionDetails;
    }

    private DelegationExternal buildDelegationExternal(String pa, String institutionTaxCode) {
        return DelegationExternal.builder()
                .id("id")
                .brokerName("brokerName")
                .brokerId(BROKER_ID)
                .institutionId("institutionId")
                .taxCode(institutionTaxCode)
                .institutionType(pa)
                .build();
    }

    private DelegationExternal buildDelegationExternal(String pa, String institutionTaxCode, String institutionName) {
        return DelegationExternal.builder()
                .id("id")
                .brokerName("brokerName")
                .brokerId(BROKER_ID)
                .institutionId("institutionId")
                .institutionName(institutionName)
                .taxCode(institutionTaxCode)
                .institutionType(pa)
                .build();
    }
}
