package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.auditing.SpringSecurityAuditorAware;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.dao.model.WrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.pagopa.backoffice.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import it.pagopa.selfcare.pagopa.backoffice.connector.security.SelfCareUser;
import org.junit.jupiter.api.AfterEach;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;

import java.util.List;
import java.util.Optional;

class WrapperConnectorImplTest {

    private final static String LOGGED_USER_ID = "id";

    private final SelfCareUser selfCareUser;
    private final WrapperRepository repositoryMock;
    private final MongoTemplate mongoTemplateMock;
    private final WrapperConnectorImpl wrapperConnector;


    public WrapperConnectorImplTest() {
        selfCareUser = SelfCareUser.builder(LOGGED_USER_ID).build();
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(selfCareUser, null);
        TestSecurityContextHolder.setAuthentication(authenticationToken);
        this.repositoryMock = mock(WrapperRepository.class);
        this.mongoTemplateMock = mock(MongoTemplate.class);
        this.wrapperConnector = new WrapperConnectorImpl(repositoryMock, mongoTemplateMock, new SpringSecurityAuditorAware());
    }


    @AfterEach
    void clear() {
        repositoryMock.deleteAll();
        reset(repositoryMock, mongoTemplateMock);
    }

    @Test
    void insert_wrapperChannel_duplicateKey() {
        // given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails entity = mockInstance(new ChannelDetails());
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        Optional<WrapperEntities> opt = Optional.of(wrapperEntities);


        doThrow(DuplicateKeyException.class)
                .when(repositoryMock)
                .insert(any(WrapperEntities.class));

        when(repositoryMock
                .save(any(WrapperEntities.class))).thenReturn(wrapperEntities);
        when(repositoryMock
                .findById(anyString())).thenReturn(opt);
        // when
        wrapperConnector.insert(entity,note,status);
        // then
//        assertEquals("Channel id = " + entity.getChannelCode(), e.getMessage());
        verify(repositoryMock, times(1))
                .save(wrapperEntities);
        verify(repositoryMock, times(1))
                .findById(anyString());
        verify(repositoryMock, times(1))
                .insert(wrapperEntities);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void insert_wrapperChannel() {
        // given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails entity = mockInstance(new ChannelDetails());
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);

        when(repositoryMock
                .insert(any(WrapperEntities.class))).thenReturn(wrapperEntities);

        // when
        WrapperEntities saved = wrapperConnector.insert(entity,note,status);
        // then
        assertEquals(wrapperEntities, saved);
        verify(repositoryMock, times(1))
                .insert(wrapperEntities);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void insert_wrapperStation_duplicateKey() {
        // given
        String note ="note";
        String status="TO_CHECK";
        StationDetails entity = mockInstance(new StationDetails());
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);

        Optional<WrapperEntities> opt = Optional.of(wrapperEntities);

        doThrow(DuplicateKeyException.class)
                .when(repositoryMock)
                .insert(any(WrapperEntities.class));

        when(repositoryMock
                .save(any(WrapperEntities.class))).thenReturn(wrapperEntities);

        when(repositoryMock
                .findById(anyString())).thenReturn(opt);

        // when
        wrapperConnector.insert(entity,note,status);
        // then
//        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class, executable);
//        assertEquals("Station id = " + entity.getStationCode(), e.getMessage());
        verify(repositoryMock, times(1))
                .save(wrapperEntities);
        verify(repositoryMock, times(1))
                .findById(anyString());
        verify(repositoryMock, times(1))
                .insert(wrapperEntities);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void insert_wrapperStation() {
        // given
        String note ="note";
        String status="TO_CHECK";
        StationDetails entity = mockInstance(new StationDetails());
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        wrapperEntities.isNew();
        wrapperEntities.setEntities(List.of(wrapperEntity));


        when(repositoryMock
                .insert(any(WrapperEntities.class))).thenReturn(wrapperEntities);
        // when
        WrapperEntities saved = wrapperConnector.insert(entity,note,status);
        // then
        assertEquals(wrapperEntities, saved);
        verify(repositoryMock, times(1))
                .insert(wrapperEntities);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findById() {
        // given
        String code = "code";
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetailsMock);
        WrapperEntities<ChannelDetails> wrapperEntities = new WrapperEntities<ChannelDetails>(wrapperEntity);

        Optional<WrapperEntities> opt = Optional.of(new WrapperEntities<ChannelDetails>(wrapperEntity));

        when(repositoryMock
                .findById(anyString())).thenReturn(opt);
        // when
        Optional<WrapperEntitiesOperations> responseOpt = wrapperConnector.findById(code);
        // then
        assertEquals(wrapperEntities, responseOpt.get());
        verify(repositoryMock, times(1))
                .findById(anyString());
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void update_wrapperChannel() {
        // given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        ChannelDetails channelDetailsMockInsert = mockInstance(new ChannelDetails());
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetailsMock);
        WrapperEntities<ChannelDetails> wrapperEntities = new WrapperEntities<ChannelDetails>(wrapperEntity);
        Optional<WrapperEntities> opt = Optional.of(new WrapperEntities<ChannelDetails>(wrapperEntity));

        when(repositoryMock
                .findById(anyString())).thenReturn(opt);

        wrapperEntities.getEntities().add(new WrapperEntity<>(channelDetailsMockInsert));

        when(repositoryMock
                .save(any(WrapperEntities.class))).thenReturn(wrapperEntities);
        // when
        WrapperEntitiesOperations saved = wrapperConnector.update(channelDetailsMockInsert,note,status);
        // then
        assertEquals(wrapperEntities, saved);
        verify(repositoryMock, times(1))
                .findById(anyString());
        verify(repositoryMock, times(1))
                .save(any());
        verifyNoMoreInteractions(repositoryMock);
    }
    @Test
    void updateByOpt_wrapperChannel() {
        // given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        ChannelDetails channelDetailsMockInsert = mockInstance(new ChannelDetails());
        WrapperEntity<ChannelDetails> wrapperEntity = new WrapperEntity<>(channelDetailsMock);
        WrapperEntities<ChannelDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        Optional<WrapperEntities> opt = Optional.of(new WrapperEntities<>(wrapperEntity));

        when(repositoryMock
                .findById(anyString())).thenReturn(opt);

        wrapperEntities.getEntities().add(new WrapperEntity<>(channelDetailsMockInsert));

        when(repositoryMock
                .save(any(WrapperEntities.class))).thenReturn(wrapperEntities);
        // when
        WrapperEntitiesOperations saved = wrapperConnector.updateByOpt(channelDetailsMockInsert,note,status);
        // then
        assertEquals(wrapperEntities, saved);
        verify(repositoryMock, times(1))
                .findById(anyString());
        verify(repositoryMock, times(1))
                .save(any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void updateByOpt_wrapperStation() {
        // given
        String note ="note";
        String status="TO_CHECK";
        StationDetails stationDetailsMock = mockInstance(new StationDetails());
        StationDetails stationDetailsMockInsert = mockInstance(new StationDetails());
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetailsMock);
        WrapperEntities<StationDetails> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        Optional<WrapperEntities> opt = Optional.of(new WrapperEntities<>(wrapperEntity));

        when(repositoryMock
                .findById(anyString())).thenReturn(opt);

        wrapperEntities.getEntities().add(new WrapperEntity<>(stationDetailsMockInsert));

        when(repositoryMock
                .save(any(WrapperEntities.class))).thenReturn(wrapperEntities);
        // when
        WrapperEntitiesOperations saved = wrapperConnector.updateByOpt(stationDetailsMockInsert,note,status);
        // then
        assertEquals(wrapperEntities, saved);
        verify(repositoryMock, times(1))
                .findById(anyString());
        verify(repositoryMock, times(1))
                .save(any());
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void update_wrapperChannel_notFound() {
        // given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails channelDetailsMockInsert = mockInstance(new ChannelDetails());
        when(repositoryMock
                .findById(anyString())).thenReturn(Optional.ofNullable(null));
        // when
        Executable executable = () -> wrapperConnector.update(channelDetailsMockInsert,note,status);
        // then
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, executable);
        verify(repositoryMock, times(1))
                .findById(anyString());

        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void update_wrapperStation() {
        // given
        String note ="note";
        String status="TO_CHECK";
        StationDetails stationDetailsMock = mockInstance(new StationDetails());
        StationDetails stationDetailsMockInsert = mockInstance(new StationDetails());
        WrapperEntity<StationDetails> wrapperEntity = new WrapperEntity<>(stationDetailsMock);
        WrapperEntities<StationDetails> wrapperEntities = new WrapperEntities<StationDetails>(wrapperEntity);
        Optional<WrapperEntities> opt = Optional.of(new WrapperEntities<StationDetails>(wrapperEntity));

        when(repositoryMock
                .findById(anyString())).thenReturn(opt);

        wrapperEntities.getEntities().add(new WrapperEntity<>(stationDetailsMockInsert));

        when(repositoryMock
                .save(any(WrapperEntities.class))).thenReturn(wrapperEntities);
        // when
        WrapperEntitiesOperations saved = wrapperConnector.update(stationDetailsMockInsert,note,status);
        // then
        assertEquals(wrapperEntities, saved);
        verify(repositoryMock, times(1))
                .findById(anyString());
        verify(repositoryMock, times(1))
                .save(any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void update_wrapperStation_notFound() {
        // given
        String note ="note";
        String status="TO_CHECK";
        StationDetails stationDetailsMockInsert = mockInstance(new StationDetails());
        when(repositoryMock
                .findById(anyString())).thenReturn(Optional.ofNullable(null));
        // when
        Executable executable = () -> wrapperConnector.update(stationDetailsMockInsert,note,status);
        // then
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, executable);
        verify(repositoryMock, times(1))
                .findById(anyString());

        verifyNoMoreInteractions(repositoryMock);
    }
}
