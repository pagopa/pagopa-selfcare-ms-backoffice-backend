package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ExternalApiClient;
import it.pagopa.selfcare.pagopa.backoffice.config.MappingsConfiguration;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionView;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.CreditorInstitutionsView;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerDelegationPage;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerDelegationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerStationPage;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.CIBrokerStationResource;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokerResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.BrokersResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationDetailsResourceList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
    private static final int PAGE_0 = 0;
    private static final int LIMIT = 5;

    @MockBean
    private ApiConfigClient apiConfigClient;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @MockBean
    private ExternalApiClient externalApiClient;

    @MockBean
    private WrapperService wrapperService;

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
                sut.getStationsDetailsListByBroker("brokerCode", "stationId", 1, PAGE_0));

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

        when(externalApiClient.getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString(), eq(null)))
                .thenReturn(delegationExternalList);

        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_1), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList(3L));
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_2), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList(3L));
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_3), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList(null));

        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_1))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_1));
        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_2))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_2));

        CIBrokerDelegationPage result = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, null, PAGE_0, LIMIT));

        assertNotNull(result);
        assertNotNull(result.getCiBrokerDelegationResources());
        assertNotNull(result.getPageInfo());

        List<CIBrokerDelegationResource> delegationResources = result.getCiBrokerDelegationResources();
        assertNotEquals(delegationExternalList.size(), delegationResources.size());
        assertEquals(3, delegationResources.size());

        Optional<CIBrokerDelegationResource> first = delegationResources.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_1))
                .findFirst();
        assertTrue(first.isPresent());
        CIBrokerDelegationResource ciBrokerDelegationResource1 = first.get();
        assertEquals(3L, ciBrokerDelegationResource1.getInstitutionStationCount());
        assertEquals(CBILL_1, ciBrokerDelegationResource1.getCbillCode());
        assertTrue(ciBrokerDelegationResource1.getIsInstitutionSignedIn());

        Optional<CIBrokerDelegationResource> second = delegationResources.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_2))
                .findFirst();
        assertTrue(second.isPresent());
        CIBrokerDelegationResource ciBrokerDelegationResource2 = second.get();
        assertEquals(3L, ciBrokerDelegationResource2.getInstitutionStationCount());
        assertEquals(CBILL_2, ciBrokerDelegationResource2.getCbillCode());
        assertTrue(ciBrokerDelegationResource2.getIsInstitutionSignedIn());

        Optional<CIBrokerDelegationResource> third = delegationResources.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_3))
                .findFirst();
        assertTrue(third.isPresent());
        CIBrokerDelegationResource ciBrokerDelegationResource3 = third.get();
        assertEquals(0L, ciBrokerDelegationResource3.getInstitutionStationCount());
        assertNull(ciBrokerDelegationResource3.getCbillCode());
        assertTrue(ciBrokerDelegationResource3.getIsInstitutionSignedIn());

        verify(externalApiClient).getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString(), eq(null));
        verify(apiConfigSelfcareIntegrationClient, times(3))
                .getStationsDetailsListByBroker(anyString(), eq(null), anyString(), anyInt(), anyInt());
        verify(apiConfigClient, times(3)).getCreditorInstitutionDetails(anyString());
    }

    @Test
    void getBrokerDelegationTestWithPagination() {
        List<DelegationExternal> delegationExternalList = new ArrayList<>();
        delegationExternalList.add(buildDelegationExternal("PA", INSTITUTION_TAX_CODE_1, "my test"));
        delegationExternalList.add(buildDelegationExternal("GSP", INSTITUTION_TAX_CODE_2, "test test"));
        delegationExternalList.add(buildDelegationExternal("PSP", "institutionTaxCode4"));

        when(externalApiClient.getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString(), anyString()))
                .thenReturn(delegationExternalList, delegationExternalList);

        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_1), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList(3L));
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_2), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList(3L));

        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_1))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_1));
        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_2))
                .thenReturn(buildCreditorInstitutionDetails(CBILL_2));

        CIBrokerDelegationPage firstPage = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, "test", PAGE_0, LIMIT));
        CIBrokerDelegationPage secondPage = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, "test", 1, LIMIT));

        assertNotNull(firstPage);
        assertNotNull(firstPage.getCiBrokerDelegationResources());
        assertNotNull(firstPage.getPageInfo());

        PageInfo firstPagePageInfo = firstPage.getPageInfo();
        assertEquals(PAGE_0, firstPagePageInfo.getPage());
        assertEquals(LIMIT, firstPagePageInfo.getLimit());
        assertEquals(1, firstPagePageInfo.getTotalPages());
        assertEquals(2, firstPagePageInfo.getTotalItems());
        assertEquals(2, firstPagePageInfo.getItemsFound());

        List<CIBrokerDelegationResource> delegationResources = firstPage.getCiBrokerDelegationResources();
        assertNotEquals(delegationExternalList.size(), delegationResources.size());
        assertEquals(2, delegationResources.size());

        assertNotNull(secondPage);
        assertNotNull(secondPage.getCiBrokerDelegationResources());
        assertNotNull(secondPage.getPageInfo());
        assertEquals(0, secondPage.getCiBrokerDelegationResources().size());

        PageInfo secondPagePageInfo = secondPage.getPageInfo();
        assertEquals(1, secondPagePageInfo.getPage());
        assertEquals(LIMIT, secondPagePageInfo.getLimit());
        assertEquals(1, secondPagePageInfo.getTotalPages());
        assertEquals(2, secondPagePageInfo.getTotalItems());
        assertEquals(0, secondPagePageInfo.getItemsFound());

        verify(externalApiClient, times(2))
                .getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString(), anyString());
        verify(apiConfigSelfcareIntegrationClient, times(2))
                .getStationsDetailsListByBroker(anyString(), eq(null), anyString(), anyInt(), anyInt());
        verify(apiConfigClient, times(2)).getCreditorInstitutionDetails(anyString());
    }

    @Test
    void getBrokerDelegationTestWithCBILCodeNotFount() {
        List<DelegationExternal> delegationExternalList = new ArrayList<>();
        delegationExternalList.add(buildDelegationExternal("PA", INSTITUTION_TAX_CODE_1));

        when(externalApiClient.getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString(), eq(null)))
                .thenReturn(delegationExternalList);

        long stationCount = 0L;
        when(apiConfigSelfcareIntegrationClient.getStationsDetailsListByBroker(
                eq(BROKER_CODE), eq(null), eq(INSTITUTION_TAX_CODE_1), anyInt(), anyInt()))
                .thenReturn(buildStationDetailsList(stationCount));

        when(apiConfigClient.getCreditorInstitutionDetails(INSTITUTION_TAX_CODE_1))
                .thenThrow(FeignException.NotFound.class);

        CIBrokerDelegationPage result = assertDoesNotThrow(
                () -> sut.getCIBrokerDelegation(BROKER_CODE, BROKER_ID, null, PAGE_0, LIMIT));

        assertNotNull(result);
        assertNotNull(result.getCiBrokerDelegationResources());
        assertNotNull(result.getPageInfo());

        List<CIBrokerDelegationResource> delegationResources = result.getCiBrokerDelegationResources();
        assertEquals(delegationExternalList.size(), delegationResources.size());
        assertEquals(1, delegationResources.size());

        Optional<CIBrokerDelegationResource> first = delegationResources.stream().filter(delegation -> delegation
                        .getInstitutionTaxCode().equals(INSTITUTION_TAX_CODE_1))
                .findFirst();
        assertTrue(first.isPresent());
        CIBrokerDelegationResource ciBrokerDelegationResource1 = first.get();
        assertEquals(stationCount, ciBrokerDelegationResource1.getInstitutionStationCount());
        assertNull(ciBrokerDelegationResource1.getCbillCode());
        assertFalse(ciBrokerDelegationResource1.getIsInstitutionSignedIn());

        verify(externalApiClient).getBrokerDelegation(eq(null), eq(BROKER_ID), anyString(), anyString(), eq(null));
        verify(apiConfigSelfcareIntegrationClient, times(1))
                .getStationsDetailsListByBroker(anyString(), eq(null), anyString(), anyInt(), anyInt());
        verify(apiConfigClient, times(1)).getCreditorInstitutionDetails(anyString());
    }

    @Test
    void getCIBrokerStationsSuccess() {
        CreditorInstitutionsView institutionsView = buildInstitutionsView();
        Instant modifiedAt = Instant.now();
        Instant activationDate = Instant.now();
        when(apiConfigClient
                .getCreditorInstitutionsAssociatedToBrokerStations(
                        anyInt(),
                        anyInt(),
                        anyString(),
                        anyString(),
                        anyString(),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null))
        ).thenReturn(institutionsView);
        when(wrapperService.findByIdOptional(anyString())).thenReturn(Optional.of(buildWrapper(modifiedAt, activationDate)));

        CIBrokerStationPage result = assertDoesNotThrow(() ->
                sut.getCIBrokerStations("brokerTaxCode", "ciTaxCode", "stationCode", 0, 5));

        assertNotNull(result);
        List<CIBrokerStationResource> ciBrokerStations = result.getCiBrokerStations();
        assertFalse(ciBrokerStations.isEmpty());
        assertEquals(1, ciBrokerStations.size());
        CIBrokerStationResource stationResource = ciBrokerStations.get(0);

        CreditorInstitutionView view = institutionsView.getCreditorInstitutionList().get(0);
        assertEquals(view.getIdDominio(), stationResource.getCiTaxCode());
        assertEquals(view.getIdIntermediarioPa(), stationResource.getBrokerTaxCode());
        assertEquals(view.getIdStazione(), stationResource.getStationCode());
        assertEquals(view.getProgressivo(), stationResource.getApplicationCode());
        assertEquals(view.getSegregazione(), stationResource.getSegregationCode());
        assertEquals(view.getAuxDigit(), stationResource.getAuxDigit());
        assertEquals(view.getStationEnabled(), stationResource.getStationEnabled());
        assertEquals(modifiedAt, stationResource.getModifiedAt());
        assertEquals(activationDate, stationResource.getActivationDate());
    }

    @Test
    void getCIBrokerStationsWithViewNoResult() {
        when(apiConfigClient
                .getCreditorInstitutionsAssociatedToBrokerStations(
                        anyInt(),
                        anyInt(),
                        anyString(),
                        anyString(),
                        anyString(),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null))
        ).thenReturn(CreditorInstitutionsView.builder().creditorInstitutionList(Collections.emptyList()).build());

        CIBrokerStationPage result = assertDoesNotThrow(() ->
                sut.getCIBrokerStations("brokerTaxCode", "ciTaxCode", "stationCode", 0, 5));

        assertNotNull(result);
        assertTrue(result.getCiBrokerStations().isEmpty());

        verify(wrapperService, never()).findById(anyString());
    }

    @Test
    void getCIBrokerStationsWithWrapperNotFound() {
        CreditorInstitutionsView institutionsView = buildInstitutionsView();
        when(apiConfigClient
                .getCreditorInstitutionsAssociatedToBrokerStations(
                        anyInt(),
                        anyInt(),
                        anyString(),
                        anyString(),
                        anyString(),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null),
                        eq(null))
        ).thenReturn(institutionsView);
        when(wrapperService.findByIdOptional(anyString())).thenReturn(Optional.empty());

        CIBrokerStationPage result = assertDoesNotThrow(() ->
                sut.getCIBrokerStations("brokerTaxCode", "ciTaxCode", "stationCode", 0, 5));

        assertNotNull(result);
        List<CIBrokerStationResource> ciBrokerStations = result.getCiBrokerStations();
        assertFalse(ciBrokerStations.isEmpty());
        assertEquals(1, ciBrokerStations.size());
        CIBrokerStationResource stationResource = ciBrokerStations.get(0);

        CreditorInstitutionView view = institutionsView.getCreditorInstitutionList().get(0);
        assertEquals(view.getIdDominio(), stationResource.getCiTaxCode());
        assertEquals(view.getIdIntermediarioPa(), stationResource.getBrokerTaxCode());
        assertEquals(view.getIdStazione(), stationResource.getStationCode());
        assertEquals(view.getProgressivo(), stationResource.getApplicationCode());
        assertEquals(view.getSegregazione(), stationResource.getSegregationCode());
        assertEquals(view.getAuxDigit(), stationResource.getAuxDigit());
        assertEquals(view.getStationEnabled(), stationResource.getStationEnabled());
        assertNull(stationResource.getModifiedAt());
        assertNull(stationResource.getActivationDate());
    }

    @Test
    void deleteCIBroker() {
        assertDoesNotThrow(() -> sut.deleteCIBroker(anyString()));
    }

    private WrapperEntities<Object> buildWrapper(Instant modifiedAt, Instant activationDate) {
        WrapperEntities<Object> entities = new WrapperEntities<>();

        entities.setModifiedAt(modifiedAt);

        StationDetails station = new StationDetails();
        station.setActivationDate(activationDate);

        WrapperEntity<Object> entity = new WrapperEntity<>();
        entity.setEntity(station);

        entities.setEntities(Collections.singletonList(entity));
        return entities;
    }

    private CreditorInstitutionsView buildInstitutionsView() {
        return CreditorInstitutionsView.builder()
                .creditorInstitutionList(Collections.singletonList(
                        CreditorInstitutionView.builder()
                                .idDominio("ciTaxCode")
                                .idIntermediarioPa("brokerTaxCode")
                                .idStazione("stationCode")
                                .auxDigit(3L)
                                .segregazione(40L)
                                .stationEnabled(true)
                                .build()
                ))
                .pageInfo(PageInfo.builder().build())
                .build();
    }

    private StationDetailsList buildStationDetailsList(Long totalItems) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalItems(totalItems);
        StationDetailsList stationDetailsList = new StationDetailsList();
        stationDetailsList.setPageInfo(pageInfo);
        return stationDetailsList;
    }

    private CreditorInstitutionDetails buildCreditorInstitutionDetails(String cbill) {
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
