package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TaxonomyConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TaxonomyServiceImpl.class)
class TaxonomyServiceImplTest {

    @Autowired
    private TaxonomyServiceImpl taxonomyService;

    @MockBean
    private TaxonomyConnector taxonomyConnectorMock;

    @Test
    void getTaxonomies() {
        //given
        Taxonomy taxonomyMock = mockInstance(new Taxonomy());
        String macroAreaName = "NameMacroArea";
        taxonomyMock.setMacroAreaName(macroAreaName);
        List<Taxonomy> taxonomiesMock = mockInstance(List.of(taxonomyMock));
        when(taxonomyConnectorMock.getTaxonomies())
                .thenReturn(taxonomiesMock);
        //when
        List<Taxonomy> response =  taxonomyService.getTaxonomies();
        //then
        verify(taxonomyConnectorMock, times(1))
                .getTaxonomies();
        assertEquals(response.get(0).getMacroAreaName(), macroAreaName);
        reflectionEqualsByName(taxonomiesMock, response);
        verifyNoMoreInteractions(taxonomyConnectorMock);
    }
}
