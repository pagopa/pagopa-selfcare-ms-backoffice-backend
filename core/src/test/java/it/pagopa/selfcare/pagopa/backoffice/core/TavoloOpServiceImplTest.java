package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TavoloOpConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.TaxonomyConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyTavoloOpEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.tavoloop.TavoloOpOperations;
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
@ContextConfiguration(classes = TavoloOpServiceImpl.class)
class TavoloOpServiceImplTest {

    @Autowired
    private TavoloOpServiceImpl tavoloOpService;

    @MockBean
    private TavoloOpConnector tavolConnectorMock;

    @Test
    void insert() {
        //given
        TavoloOp tavoloOpMock = mockInstance(new TavoloOp());
        DummyTavoloOpEntity dummyTavoloOpEntity = mockInstance(new DummyTavoloOpEntity());
        dummyTavoloOpEntity.setName("name");
        String name = "name";
        tavoloOpMock.setName(name);

        when(tavolConnectorMock.insert(any())).thenReturn(dummyTavoloOpEntity);
        //when
        TavoloOpOperations response =  tavoloOpService.insert(tavoloOpMock);
        //then
        verify(tavolConnectorMock, times(1))
                .insert(any());
        assertEquals(response.getName(), name);
        reflectionEqualsByName(tavoloOpMock, response);
        verifyNoMoreInteractions(tavolConnectorMock);
    }
}
