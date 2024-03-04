package it.pagopa.selfcare.pagopa.backoffice.scheduler;

import it.pagopa.selfcare.pagopa.backoffice.client.TaxonomyClient;
import it.pagopa.selfcare.pagopa.backoffice.entity.BrokerInstitutionsEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyGroupEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.client.TaxonomyDTO;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyGroupRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class TaxonomiesExtractionSchedulerTest {

    @MockBean
    private TaxonomyGroupRepository taxonomyGroupRepository;

    @MockBean
    private TaxonomyRepository taxonomyRepository;

    @MockBean
    private TaxonomyClient taxonomyClient;

    @Captor
    ArgumentCaptor<List<TaxonomyGroupEntity>> taxonomyGroupEntityCaptor;

    @Captor
    ArgumentCaptor<List<TaxonomyEntity>> taxonomyEntityCaptor;

    @Autowired
    @InjectMocks
    private TaxonomiesExtractionScheduler scheduler;

    @BeforeEach
    public void init() {
        Mockito.reset(taxonomyClient, taxonomyRepository, taxonomyGroupRepository);
    }

    @Test
    void extractTaxinomiesShouldSaveDataOnOK() {
        when(taxonomyClient.getTaxonomies()).thenReturn(Collections.singletonList(createDto()));
        scheduler.extractTaxinomies();
        verify(taxonomyClient).getTaxonomies();
        verify(taxonomyRepository).deleteAll();
        verify(taxonomyGroupRepository).deleteAll();
        verify(taxonomyRepository).saveAll(taxonomyEntityCaptor.capture());
        verify(taxonomyGroupRepository).saveAll(taxonomyGroupEntityCaptor.capture());
        assertEquals(1, taxonomyEntityCaptor.getValue().size());
        assertEquals(1, taxonomyGroupEntityCaptor.getValue().size());
        assertEquals(1, taxonomyGroupEntityCaptor.getValue().get(0).getAreas().size());
    }

    @Test
    void extractTaxinomiesShouldntSaveOnRecoveryKO() {
        when(taxonomyClient.getTaxonomies()).then(invocation -> {
            throw new Exception();
        });
        assertThrows(Exception.class, () -> scheduler.extractTaxinomies());
        verify(taxonomyClient).getTaxonomies();
        verifyNoInteractions(taxonomyRepository);
        verifyNoInteractions(taxonomyGroupRepository);
    }

    public TaxonomyDTO createDto() {
        TaxonomyDTO taxonomyDTO = new TaxonomyDTO();
        taxonomyDTO.setEcType("ecType");
        taxonomyDTO.setMacroAreaEcProgressive("01");
        taxonomyDTO.setMacroAreaName("area");
        taxonomyDTO.setSpecificBuiltInData("taxonomyCode");
        return taxonomyDTO;
    }

}