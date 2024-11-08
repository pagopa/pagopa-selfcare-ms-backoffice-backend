package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStation;
import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityStations;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.BrokerCreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution.CreditorInstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.WrapperStationsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CiBrokerExtractionScheduler.class)
class CiBrokerExtractionSchedulerTest {

    private static final String BROKER_CODE = "12345";
    private static final String BROKER_CODE_2 = "broker2";
    private static final String STATION_CODE_1 = "stationCode1";
    private static final String STATION_CODE_2 = "stationCode2";

    @MockBean
    private AllPages allPages;

    @MockBean
    private BrokerInstitutionsRepository brokerInstitutionsRepository;

    @MockBean
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    @MockBean
    private WrapperStationsRepository wrapperStationsRepository;

    @Captor
    ArgumentCaptor<BrokerInstitutionsEntity> captor;

    @Autowired
    private CiBrokerExtractionScheduler scheduler;

    @Test
    void extractCiSuccess() {
        when(allPages.getAllBrokers()).thenReturn(Set.of(BROKER_CODE, BROKER_CODE_2));
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsAssociatedToBroker(anyInt(), anyInt(), eq(true), anyString()))
                .thenReturn(buildBrokerCreditorInstitutionDetails(STATION_CODE_1))
                .thenReturn(buildBrokerCreditorInstitutionDetails(STATION_CODE_2));
        when(brokerInstitutionsRepository.findByBrokerCode(anyString()))
                .thenReturn(buildBrokerInstitutionsEntity(BROKER_CODE))
                .thenReturn(buildBrokerInstitutionsEntity(BROKER_CODE_2));
        when(wrapperStationsRepository.findByIdAndType(STATION_CODE_1, WrapperType.STATION))
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(Optional.empty())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations());
        when(wrapperStationsRepository.findByIdAndType(STATION_CODE_2, WrapperType.STATION))
                .thenReturn(Optional.of(new WrapperEntityStations()))
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations());

        assertDoesNotThrow(() -> scheduler.extractCI());

        verify(brokerInstitutionsRepository, times(2)).delete(any());
        verify(brokerInstitutionsRepository, times(2)).save(any());
        verify(brokerInstitutionsRepository, times(2)).updateBrokerInstitutionsList(anyString(), anyList());
        verify(brokerInstitutionsRepository).deleteAllByCreatedAtBefore(any());
    }

    @Test
    void extractCiSuccessButOneBrokerExtractionFail() {
        when(allPages.getAllBrokers()).thenReturn(Set.of(BROKER_CODE, BROKER_CODE_2));
        when(apiConfigSelfcareIntegrationClient.getCreditorInstitutionsAssociatedToBroker(anyInt(), anyInt(), eq(true), anyString()))
                .thenReturn(buildBrokerCreditorInstitutionDetails(STATION_CODE_1))
                .thenReturn(buildBrokerCreditorInstitutionDetails(STATION_CODE_2));
        when(brokerInstitutionsRepository.findByBrokerCode(anyString()))
                .thenReturn(buildBrokerInstitutionsEntity(BROKER_CODE))
                .thenReturn(buildBrokerInstitutionsEntity(BROKER_CODE_2));
        when(wrapperStationsRepository.findByIdAndType(STATION_CODE_1, WrapperType.STATION))
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(Optional.empty())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations());
        when(wrapperStationsRepository.findByIdAndType(STATION_CODE_2, WrapperType.STATION))
                .thenReturn(Optional.of(new WrapperEntityStations()))
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations())
                .thenReturn(buildOptionalWrapperEntityStations());
        doThrow(RuntimeException.class)
                .when(brokerInstitutionsRepository).updateBrokerInstitutionsList(eq(BROKER_CODE_2), anyList());

        assertDoesNotThrow(() -> scheduler.extractCI());

        verify(brokerInstitutionsRepository, times(2)).delete(any());
        verify(brokerInstitutionsRepository, times(2)).save(any());
        verify(brokerInstitutionsRepository, times(2)).updateBrokerInstitutionsList(anyString(), anyList());
        verify(brokerInstitutionsRepository).deleteAllByCreatedAtBefore(any());
    }

    private Optional<WrapperEntityStations> buildOptionalWrapperEntityStations() {
        WrapperEntityStations wrapperEntityStations = new WrapperEntityStations();
        StationDetails stationDetails = new StationDetails();
        stationDetails.setActivationDate(Instant.now());
        WrapperEntityStation entityStation = WrapperEntityStation.builder().entity(stationDetails).build();
        wrapperEntityStations.setEntities(List.of(entityStation));

        return Optional.of(wrapperEntityStations);
    }

    private BrokerCreditorInstitutionDetails buildBrokerCreditorInstitutionDetails(String stationCode) {
        return BrokerCreditorInstitutionDetails.builder()
                .creditorInstitutions(List.of(
                        buildCreditorInstitutionDetail(stationCode, 1L, "13", null),
                        buildCreditorInstitutionDetail(stationCode, null, null, "22"),
                        buildCreditorInstitutionDetail(stationCode, null, "13", null),
                        buildCreditorInstitutionDetail(stationCode, null, "13", "22"),
                        buildCreditorInstitutionDetail(stationCode, null, null, null)
                ))
                .pageInfo(PageInfo.builder().totalItems(5L).build()).build();
    }

    private CreditorInstitutionDetail buildCreditorInstitutionDetail(
            String stationCode,
            Long auxDigit,
            String segregationCode,
            String applicationCode
    ) {
        return CreditorInstitutionDetail.builder()
                .businessName("businessName")
                .creditorInstitutionCode("(")
                .brokerBusinessName("brokerBusinessName")
                .brokerCode("brokerCode")
                .auxDigit(auxDigit)
                .segregationCode(segregationCode)
                .applicationCode(applicationCode)
                .cbillCode("cbillCode")
                .stationCode(stationCode)
                .stationEnabled(true)
                .endpointRT("endpointRT")
                .endpointRedirect("endpointRedirect")
                .endpointMU("endpointMU")
                .versionePrimitive(1)
                .ciStatus(true)
                .stationVersion(1L)
                .broadcast(true)
                .pspPayment(false)
                .build();
    }

    private Optional<BrokerInstitutionsEntity> buildBrokerInstitutionsEntity(String brokerCode) {
        return Optional.of(
                BrokerInstitutionsEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .brokerCode(brokerCode)
                        .institutions(List.of(BrokerInstitutionEntity.builder()
                                .taxCode("99999")
                                .version("2")
                                .segregationCode("9999_01")
                                .stationState("true")
                                .build()))
                        .build()
        );
    }
}
