package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CiBrokerExtractionSchedulerTest {


    @MockBean
    private AllPages allPages;


    @MockBean
    private BrokerInstitutionsRepository brokerInstitutionsRepository;


    @Autowired
    @InjectMocks
    private CiBrokerExtractionScheduler scheduler;

    @Captor
    ArgumentCaptor<BrokerInstitutionsEntity> captor;


    @Test
    void extractCi() throws IOException {
        final String BROKER_CODE = "12345";
        when(brokerInstitutionsRepository.findByBrokerCode(anyString())).thenReturn(Optional.ofNullable(BrokerInstitutionsEntity.builder()
                .id("1")
                .brokerCode(BROKER_CODE)
                .institutions(List.of(BrokerInstitutionEntity.builder()
                        .taxCode("99999")
                        .version("2")
                        .segregationCode("9999_01")
                        .stationState("true")
                        .build()))
                .build()));
        doNothing().when(brokerInstitutionsRepository).delete(any());
        when(allPages.getAllBrokers()).thenReturn(Set.of(BROKER_CODE));
        when(allPages.getCreditorInstitutionsAssociatedToBroker(eq(BROKER_CODE), anyBoolean())).thenReturn(Set.of(BrokerInstitutionEntity.builder()
                .taxCode("99999")
                .stationId("9999_01")
                .version("2")
                .stationState("true")
                .build()));
        scheduler.extractCi();
        verify(brokerInstitutionsRepository).save(captor.capture());
        assertEquals(BROKER_CODE, captor.getValue().getBrokerCode());
        assertEquals(1, captor.getValue().getInstitutions().size());
        assertEquals("9999_01", captor.getValue().getInstitutions().get(0).getStationId());

    }
}
