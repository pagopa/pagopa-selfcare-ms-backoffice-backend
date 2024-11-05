package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.repository.BrokerInstitutionsRepository;
import it.pagopa.selfcare.pagopa.backoffice.scheduler.function.AllPages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(allPages.getAllBrokers()).thenReturn(Set.of(BROKER_CODE));
        when(allPages.getCreditorInstitutionsAssociatedToBroker(BROKER_CODE))
                .thenReturn(CompletableFuture.runAsync(() -> CompletableFuture.completedFuture("test")));
        assertDoesNotThrow(() -> scheduler.extractCi());
    }

    @Test
    void extractCiFail() {
        when(allPages.getAllBrokers()).thenReturn(Set.of(BROKER_CODE));
        when(allPages.getCreditorInstitutionsAssociatedToBroker(BROKER_CODE)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> scheduler.extractCi());
    }
}
