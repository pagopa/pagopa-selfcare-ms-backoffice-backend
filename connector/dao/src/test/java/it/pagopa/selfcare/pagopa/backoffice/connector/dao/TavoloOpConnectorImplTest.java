package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.auditing.SpringSecurityAuditorAware;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.TavoloOp;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.WrapperEntitiesList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TavoloOpConnectorImplTest {

    private final static String LOGGED_USER_ID = "id";

    private final SelfCareUser selfCareUser;
    private final TavoloOpRepository repositoryMock;
    private final MongoTemplate mongoTemplateMock;
    private final TavoloOpConnectorImpl tavoloOpConnector;


    public TavoloOpConnectorImplTest() {
        selfCareUser = SelfCareUser.builder(LOGGED_USER_ID).build();
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(selfCareUser, null);
        TestSecurityContextHolder.setAuthentication(authenticationToken);
        this.repositoryMock = mock(TavoloOpRepository.class);
        this.mongoTemplateMock = mock(MongoTemplate.class);
        this.tavoloOpConnector = new TavoloOpConnectorImpl(new SpringSecurityAuditorAware(),repositoryMock);
    }


    @AfterEach
    void clear() {
        repositoryMock.deleteAll();
        reset(repositoryMock, mongoTemplateMock);
    }

    @Test
    void insert_tavoloOp_duplicateKey() {
        // given

        TavoloOp entity = mockInstance(new TavoloOp());

        Optional<TavoloOp> opt = Optional.of(entity);


        doThrow(DuplicateKeyException.class)
                .when(repositoryMock)
                .insert(any(TavoloOp.class));



              when(repositoryMock
                      .insert(any(TavoloOp.class))).thenReturn(entity);
        // when
        TavoloOp saved = (TavoloOp) tavoloOpConnector.insert(entity);
        // then
        assertEquals(entity, saved);
        verify(repositoryMock, times(1))
                .insert(entity);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void insert_tavoloOp() {
        // given

        TavoloOp entity = mockInstance(new TavoloOp());


        when(repositoryMock
                .insert(any(TavoloOp.class))).thenReturn(entity);

        // when
        TavoloOp saved = (TavoloOp) tavoloOpConnector.insert(entity);
        // then
        assertEquals(entity, saved);
        verify(repositoryMock, times(1))
                .insert(entity);
        verifyNoMoreInteractions(repositoryMock);
    }


}
