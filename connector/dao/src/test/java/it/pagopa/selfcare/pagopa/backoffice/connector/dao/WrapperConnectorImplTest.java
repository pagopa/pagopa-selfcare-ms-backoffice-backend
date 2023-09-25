package it.pagopa.selfcare.pagopa.backoffice.connector.dao;

import it.pagopa.selfcare.pagopa.backoffice.connector.dao.auditing.SpringSecurityAuditorAware;
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
        this.wrapperConnector = new WrapperConnectorImpl(repositoryMock, new SpringSecurityAuditorAware());
    }


    @AfterEach
    void clear() {
        repositoryMock.deleteAll();
        reset(repositoryMock, mongoTemplateMock);
    }

    @Test
    void insert_wrapperChannel_duplicateKey() {
        // given
        String note = "note";
        String status = "TO_CHECK";
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
        wrapperConnector.insert(entity, note, status);
        // then
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
        String note = "note";
        String status = "TO_CHECK";
        ChannelDetails entity = mockInstance(new ChannelDetails());
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);

        when(repositoryMock
                .insert(any(WrapperEntities.class))).thenReturn(wrapperEntities);

        // when
        WrapperEntities saved = wrapperConnector.insert(entity, note, status);
        // then
        assertEquals(wrapperEntities, saved);
        verify(repositoryMock, times(1))
                .insert(wrapperEntities);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void insert_wrapperStation_duplicateKey() {
        // given
        String note = "note";
        String status = "TO_CHECK";
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
        wrapperConnector.insert(entity, note, status);
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
        String note = "note";
        String status = "TO_CHECK";
        StationDetails entity = mockInstance(new StationDetails());
        WrapperEntity<Object> wrapperEntity = new WrapperEntity<>(entity);
        WrapperEntities<Object> wrapperEntities = new WrapperEntities<>(wrapperEntity);
        wrapperEntities.isNew();
        wrapperEntities.setEntities(List.of(wrapperEntity));


        when(repositoryMock
                .insert(any(WrapperEntities.class))).thenReturn(wrapperEntities);
        // when
        WrapperEntities<StationDetails> saved = wrapperConnector.insert(entity, note, status);
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
        String note = "note";
        String status = "TO_CHECK";
        String createdBy = "createdBy";
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
        WrapperEntitiesOperations<ChannelDetails> saved = wrapperConnector.update(channelDetailsMockInsert, note, status, createdBy);
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
        String note = "note";
        String status = "TO_CHECK";
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
        WrapperEntitiesOperations<ChannelDetails> saved = wrapperConnector.updateByOpt(channelDetailsMockInsert, note, status);
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
        String note = "note";
        String status = "TO_CHECK";
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
        WrapperEntitiesOperations<StationDetails> saved = wrapperConnector.updateByOpt(stationDetailsMockInsert, note, status);
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
        String note = "note";
        String status = "TO_CHECK";
        String createdBy = "createdBy";
        ChannelDetails channelDetailsMockInsert = mockInstance(new ChannelDetails());
        when(repositoryMock
                .findById(anyString())).thenReturn(Optional.ofNullable(null));
        // when
        Executable executable = () -> wrapperConnector.update(channelDetailsMockInsert, note, status, createdBy);
        // then
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, executable);
        verify(repositoryMock, times(1))
                .findById(anyString());

        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void update_wrapperStation() {
        // given
        String note = "note";
        String status = "TO_CHECK";
        String createdBy = "createdBy";
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
        WrapperEntitiesOperations<StationDetails> saved = wrapperConnector.update(stationDetailsMockInsert, note, status, createdBy);

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
        String note = "note";
        String status = "TO_CHECK";
        String createdBy = "createdBy";
        StationDetails stationDetailsMockInsert = mockInstance(new StationDetails());
        when(repositoryMock
                .findById(anyString())).thenReturn(Optional.ofNullable(null));
        // when
        Executable executable = () -> wrapperConnector.update(stationDetailsMockInsert, note, status, createdBy);
        // then
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, executable);
        verify(repositoryMock, times(1))
                .findById(anyString());

        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike() {
        // given
        WrapperStatus status = WrapperStatus.TO_CHECK;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = "brokerCode";
        String idLike = "idLike";
        Integer page = 0;
        Integer size = 50;
        String sorting = "DESC";

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByStatusAndTypeAndBrokerCodeAndIdLike(any(),any(),anyString(),anyString(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByStatusAndTypeAndBrokerCodeAndIdLike(any(),any(),anyString(),anyString(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike_nullStatus() {
        // given
        WrapperStatus status = null;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = "brokerCode";
        String idLike = "idLike";
        Integer page = 0;
        Integer size = 50;
        String sorting = "DESC";

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByTypeAndBrokerCodeAndIdLike(any(),anyString(),anyString(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByTypeAndBrokerCodeAndIdLike(any(),anyString(),anyString(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike_nullBrokerCode() {
        // given
        WrapperStatus status = WrapperStatus.TO_CHECK;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = null;
        String idLike = "idLike";
        Integer page = 0;
        Integer size = 50;
        String sorting = "DESC";

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByStatusAndTypeAndIdLike(any(),any(),anyString(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByStatusAndTypeAndIdLike(any(),any(),anyString(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike_nullStatus_nullBrokerCode() {
        // given
        WrapperStatus status = null;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = null;
        String idLike = "idLike";
        Integer page = 0;
        Integer size = 50;
        String sorting = "DESC";

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByTypeAndIdLike(any(),anyString(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByTypeAndIdLike(any(),anyString(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike_nullIdLike() {
        // given
        WrapperStatus status = WrapperStatus.TO_CHECK;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = "brokerCode";
        String idLike = null;
        Integer page = 0;
        Integer size = 50;
        String sorting = null;

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByStatusAndTypeAndBrokerCode(any(),any(),anyString(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByStatusAndTypeAndBrokerCode(any(),any(),anyString(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike_nullStatus_nullIdLike() {
        // given
        WrapperStatus status = null;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = "brokerCode";
        String idLike = null;
        Integer page = 0;
        Integer size = 50;
        String sorting = null;

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByTypeAndBrokerCode(any(),anyString(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByTypeAndBrokerCode(any(),anyString(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike_nullIdLikeAndBrokerCode() {
        // given
        WrapperStatus status = WrapperStatus.TO_CHECK;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = null;
        String idLike = null;
        Integer page = 0;
        Integer size = 50;
        String sorting = null;

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByStatusAndType(any(),any(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByStatusAndType(any(),any(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByStatusAndTypeAndAndBrokerCodeAndIdLike_nullStatus_nullIdLikeAndBrokerCode() {
        // given
        WrapperStatus status = null;
        WrapperType wrapperType = WrapperType.CHANNEL;
        String brokerCode = null;
        String idLike = null;
        Integer page = 0;
        Integer size = 50;
        String sorting = null;

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);

        when(repositoryMock
                .findByType(any(),any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByStatusAndTypeAndBrokerCodeAndIdLike(status,wrapperType,brokerCode,idLike,page,size,sorting);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByType(any(),any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByIdAndType() {
        // given
        WrapperType wrapperType = WrapperType.STATION;
        String stationCode = "stationCode";
        String brokerCode = "brokerCode";
        Integer page = 0;
        Integer size = 50;

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);


        when(repositoryMock
                .findByIdLikeAndTypeAndBrokerCode(anyString(),any(),anyString(), any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByIdLikeOrTypeOrBrokerCode(stationCode,wrapperType, brokerCode, page, size);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByIdLikeAndTypeAndBrokerCode(anyString(),any(),anyString(), any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByIdAndType_nullId_nullBrokerCode() {
        // given
        WrapperType wrapperType = WrapperType.STATION;
        String stationCode = null;
        Integer page = 0;
        Integer size = 50;
        String brokerCode = null;

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);
        when(repositoryMock
                .findByType(any(), any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByIdLikeOrTypeOrBrokerCode(stationCode,wrapperType, brokerCode, page, size);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByType(any(), any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByIdAndType_nullId() {
        // given
        WrapperType wrapperType = WrapperType.STATION;
        String stationCode = null;
        Integer page = 0;
        Integer size = 50;
        String brokerCode = "brokerCode";

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);
        when(repositoryMock
                .findByTypeAndBrokerCode(any(), any(), any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByIdLikeOrTypeOrBrokerCode(stationCode,wrapperType, brokerCode, page, size);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByTypeAndBrokerCode(any(), any(), any());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByIdAndType_nullBrokerCode() {
        // given
        WrapperType wrapperType = WrapperType.STATION;
        String stationCode = "stationCode";
        Integer page = 0;
        Integer size = 50;
        String brokerCode = null;

        Page<WrapperEntitiesOperations<?>> paginatedMock =  mock(Page.class);
        when(repositoryMock
                .findByIdLikeAndType(any(), any(), any()))
                .thenReturn(paginatedMock);

        // when
        WrapperEntitiesList
                response =   wrapperConnector
                .findByIdLikeOrTypeOrBrokerCode(stationCode,wrapperType, brokerCode, page, size);
        // then
        assertEquals(response.getWrapperEntities(), paginatedMock.getContent());
        verify(repositoryMock, times(1))
                .findByIdLikeAndType(any(), any(), any());
        verifyNoMoreInteractions(repositoryMock);
    }
}
