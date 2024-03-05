package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyGroupAreaEntity;
import it.pagopa.selfcare.pagopa.backoffice.entity.TaxonomyGroupEntity;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.TaxonomyGroupArea;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.TaxonomyGroups;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyGroupRepository;
import it.pagopa.selfcare.pagopa.backoffice.repository.TaxonomyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxonomyServiceTest {

    @Mock
    private TaxonomyGroupRepository taxonomyGroupRepository;

    @Mock
    private TaxonomyRepository taxonomyRepository;

    @InjectMocks
    private TaxonomyService sut;

    @Spy
    ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        Mockito.reset(taxonomyRepository, taxonomyGroupRepository);
    }

    @Test
    void getTaxonomyShouldReturnDataOnOkRecovery() {
        when(taxonomyRepository.searchTaxonomies(any(),any(),any(),any(),any())).thenReturn(
                Collections.singletonList(TaxonomyEntity.builder().ecType("ecType").ecTypeCode("ecTypeCode").build()));
        Taxonomies taxonomies = assertDoesNotThrow(() -> sut.getTaxonomies(null, null, null, false));
        assertNotNull(taxonomies);
        assertNotNull(taxonomies.getTaxonomies());
        assertEquals(1, taxonomies.getTaxonomies().size());
        assertEquals("ecTypeCode",taxonomies.getTaxonomies().get(0).getEcTypeCode());
        verify(taxonomyRepository).searchTaxonomies(any(),any(),any(),any(),any());
    }

    @Test
    void getTaxonomyShouldThrowExceptionOnKO() {
        when(taxonomyRepository.searchTaxonomies(any(),any(),any(),any(),any())).then(invocationOnMock -> {
            throw new Exception();
        });
        assertThrows(Exception.class, () -> sut.getTaxonomies(null,null,null,false));
        verify(taxonomyRepository).searchTaxonomies(any(),any(),any(),any(),any());
    }

    @Test
    void getTaxonomyGroupsShouldReturnDataOnOkRecovery() {
        when(taxonomyGroupRepository.findAll()).thenReturn(
                Collections.singletonList(
                        TaxonomyGroupEntity.builder().ecType("ecType").ecTypeCode("ecTypeCode")
                                .areas(Collections.singleton(
                                        TaxonomyGroupAreaEntity.builder()
                                                .macroAreaName("macroArea")
                                                .macroAreaEcProgressive("progressive").build())
                                ).build()
                )
        );
        TaxonomyGroups taxonomyGroups = assertDoesNotThrow(() -> sut.getTaxonomyGroups());
        assertNotNull(taxonomyGroups);
        assertNotNull(taxonomyGroups.getTaxonomyGroups());
        assertEquals(1, taxonomyGroups.getTaxonomyGroups().size());
        assertEquals("ecTypeCode",taxonomyGroups.getTaxonomyGroups().get(0).getEcTypeCode());
        assertNotNull(taxonomyGroups.getTaxonomyGroups().get(0).getAreas());
        assertEquals(1, taxonomyGroups.getTaxonomyGroups().get(0).getAreas().size());
        verify(taxonomyGroupRepository).findAll();
    }

    @Test
    void getTaxonomyGroupsShouldThrowExceptionOnKO() {
        when(taxonomyGroupRepository.findAll()).then(invocationOnMock -> {
            throw new Exception();
        });
        assertThrows(Exception.class, () -> sut.getTaxonomyGroups());
        verify(taxonomyGroupRepository).findAll();
    }

}