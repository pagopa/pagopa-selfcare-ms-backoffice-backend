package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Broker;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStation;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.core.*;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.WrapperStationDetailsDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {StationController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        TaxonomyController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class TaxonomyControllerTest {
    private static final String BASE_URL = "/taxonomy";
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private ApiConfigSelfcareIntegrationService apiConfigSelfcareIntegrationService;

    @MockBean
    private TaxonomyService taxonomyServiceMock;

    @Test
    void getTaxonomy() throws Exception {
        //given
        Taxonomy taxonomy =  new Taxonomy();
        taxonomy.setMacroAreaName("name");
        List<Taxonomy> taxonomies = mockInstance(List.of(new Taxonomy()));


        when(taxonomyServiceMock.getTaxonomies())
                .thenReturn(taxonomies);
        //when
        mvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", everyItem(notNullValue()))
             );
        //then
        verify(taxonomyServiceMock, times(1))
                .getTaxonomies();
        verifyNoMoreInteractions(taxonomyServiceMock);
    }


}



