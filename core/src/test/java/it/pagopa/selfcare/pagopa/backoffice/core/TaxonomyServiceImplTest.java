package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.TaxonomyConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomies;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigServiceImpl.CREDITOR_INSTITUTION_CODE_IS_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
