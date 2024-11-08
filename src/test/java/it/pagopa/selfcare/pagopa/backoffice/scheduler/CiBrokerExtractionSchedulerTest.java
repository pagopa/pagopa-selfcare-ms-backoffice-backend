package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CiBrokerExtractionScheduler.class)
class CiBrokerExtractionSchedulerTest {

    private static final String BROKER_CODE = "12345";

    @MockBean
    private AllPages allPages;

    @MockBean
    private BrokerInstitutionsRepository brokerInstitutionsRepository;

    @Autowired
    private CiBrokerExtractionScheduler scheduler;

    @Test
    void extractCiSuccess() {
        when(allPages.getAllBrokers()).thenReturn(Set.of(BROKER_CODE, "broker2"));

        assertDoesNotThrow(() -> scheduler.extractCI());

        verify(allPages, times(2)).upsertCreditorInstitutionsAssociatedToBroker(anyString());
    }

    @Test
    void extractCiFail() {
        when(allPages.getAllBrokers()).thenReturn(Set.of(BROKER_CODE, "broker2"));
        doThrow(RuntimeException.class).when(allPages).upsertCreditorInstitutionsAssociatedToBroker(BROKER_CODE);

        assertDoesNotThrow(() -> scheduler.extractCI());

        verify(allPages, times(2)).upsertCreditorInstitutionsAssociatedToBroker(anyString());
    }
}
