package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.WrapperConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperEntitiesOperations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WrapperServiceImpl.class)
public class WrapperServiceImplTest {

    @Autowired
    private WrapperServiceImpl wrapperService;

    @MockBean
    private WrapperConnector wrapperConnectorMock;


    @Test
    void createWrapperChannelDetails(){
        //given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        DummyWrapperEntity<ChannelDetails> wrapperEntity = new DummyWrapperEntity<>(channelDetailsMock);
        DummyWrapperEntities<ChannelDetails> wrapperEntities = new DummyWrapperEntities<>(wrapperEntity);
        wrapperEntities.setEntities(List.of(wrapperEntity));
        when(wrapperConnectorMock.insert((ChannelDetails) any(),anyString(),anyString()))
                .thenReturn((wrapperEntities));
        //when
        WrapperEntitiesOperations<ChannelDetails> wrapperEntitiesOperations = wrapperService.createWrapperChannelDetails(channelDetailsMock,note,status);
        //then
        assertNotNull(wrapperEntitiesOperations);

        assertEquals(wrapperEntitiesOperations.getWrapperEntityOperationsSortedList().get(0).getEntity().getChannelCode(), channelDetailsMock.getChannelCode());
        reflectionEqualsByName(wrapperEntities.getWrapperEntityOperationsSortedList().get(0).getEntity(), channelDetailsMock);
        verify(wrapperConnectorMock, times(1))
                .insert(channelDetailsMock,note,status);
        verifyNoMoreInteractions(wrapperConnectorMock);

    }

    @Test
    void createWrapperStationDetails(){
        //given
        String note ="note";
        String status="TO_CHECK";
        StationDetails stationDetailsMock = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = new DummyWrapperEntity<>(stationDetailsMock);
        DummyWrapperEntities<StationDetails> wrapperEntities = new DummyWrapperEntities<>(wrapperEntity);
        wrapperEntities.setEntities(List.of(wrapperEntity));
        when(wrapperConnectorMock.insert((StationDetails) any(),anyString(),anyString()))
                .thenReturn((wrapperEntities));
        //when
        WrapperEntitiesOperations<StationDetails> wrapperEntitiesOperations = wrapperService.createWrapperStationDetails(stationDetailsMock,note,status);
        //then
        assertNotNull(wrapperEntitiesOperations);
        assertEquals(wrapperEntitiesOperations.getWrapperEntityOperationsSortedList().get(0).getEntity().getStationCode(), stationDetailsMock.getStationCode());
        reflectionEqualsByName(wrapperEntities.getWrapperEntityOperationsSortedList().get(0).getEntity(), stationDetailsMock);
        verify(wrapperConnectorMock, times(1))
                .insert(stationDetailsMock,note,status);
        verifyNoMoreInteractions(wrapperConnectorMock);

    }
    @Test
    void findById(){
        //given
        String code = "code";
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        DummyWrapperEntity<ChannelDetails> wrapperEntity = new DummyWrapperEntity<>(channelDetailsMock);
        DummyWrapperEntities<ChannelDetails> wrapperEntities = new DummyWrapperEntities<>(wrapperEntity);

        Optional<WrapperEntitiesOperations> opt = Optional.of(new DummyWrapperEntities<>(wrapperEntity));

        when(wrapperConnectorMock.findById(anyString()))
                .thenReturn(opt);
        //when
        WrapperEntitiesOperations response = wrapperService.findById(code);
        //then
        assertNotNull(response);
        assertEquals(((DummyWrapperEntity) response.getWrapperEntityOperationsSortedList().get(0)).getEntity(), channelDetailsMock);
        reflectionEqualsByName(wrapperEntities.getWrapperEntityOperationsSortedList().get(0).getEntity(), channelDetailsMock);
        verify(wrapperConnectorMock, times(1))
                .findById(code);
        verifyNoMoreInteractions(wrapperConnectorMock);
    }

    @Test
    void updateWrapperChannelDetails(){
        //given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        DummyWrapperEntity<ChannelDetails> wrapperEntity = new DummyWrapperEntity<>(channelDetailsMock);
        DummyWrapperEntities<ChannelDetails> wrapperEntities = new DummyWrapperEntities<>(wrapperEntity);

        when(wrapperConnectorMock.update((ChannelDetails) any(),anyString(),anyString()))
                .thenReturn(wrapperEntities);
        //when
        WrapperEntitiesOperations response = wrapperService.updateWrapperChannelDetails(channelDetailsMock,note,status);
        //then
        assertNotNull(response);
        assertEquals(((DummyWrapperEntity) response.getWrapperEntityOperationsSortedList().get(0)).getEntity(), channelDetailsMock);
        reflectionEqualsByName(wrapperEntities.getWrapperEntityOperationsSortedList().get(0).getEntity(), channelDetailsMock);
        verify(wrapperConnectorMock, times(1))
                .update(channelDetailsMock,note,status);
        verifyNoMoreInteractions(wrapperConnectorMock);
    }

    @Test
    void updateWrapperStationDetails(){
        //given
        String note ="note";
        String status="TO_CHECK";
        StationDetails stationDetailsMock = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = new DummyWrapperEntity<>(stationDetailsMock);
        DummyWrapperEntities<StationDetails> wrapperEntities = new DummyWrapperEntities<>(wrapperEntity);

        when(wrapperConnectorMock.update((StationDetails) any(),anyString(),anyString()))
                .thenReturn(wrapperEntities);
        //when
        WrapperEntitiesOperations response = wrapperService.updateWrapperStationDetails(stationDetailsMock,note,status);
        //then
        assertNotNull(response);
        assertEquals(((DummyWrapperEntity) response.getWrapperEntityOperationsSortedList().get(0)).getEntity(), stationDetailsMock);
        reflectionEqualsByName(wrapperEntities.getWrapperEntityOperationsSortedList().get(0).getEntity(), stationDetailsMock);
        verify(wrapperConnectorMock, times(1))
                .update(stationDetailsMock,note,status);
        verifyNoMoreInteractions(wrapperConnectorMock);
    }

    @Test
    void updateWrapperChannelDetailsByOpt(){
        //given
        String note ="note";
        String status="TO_CHECK";
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        DummyWrapperEntity<ChannelDetails> wrapperEntity = new DummyWrapperEntity<>(channelDetailsMock);
        DummyWrapperEntities<ChannelDetails> wrapperEntities = new DummyWrapperEntities<>(wrapperEntity);

        when(wrapperConnectorMock.updateByOpt((ChannelDetails) any(),anyString(),anyString()))
                .thenReturn(wrapperEntities);
        //when
        WrapperEntitiesOperations response = wrapperService.updateWrapperChannelDetailsByOpt(channelDetailsMock,note,status);
        //then
        assertNotNull(response);
        assertEquals(((DummyWrapperEntity) response.getWrapperEntityOperationsSortedList().get(0)).getEntity(), channelDetailsMock);
        reflectionEqualsByName(wrapperEntities.getWrapperEntityOperationsSortedList().get(0).getEntity(), channelDetailsMock);
        verify(wrapperConnectorMock, times(1))
                .updateByOpt(channelDetailsMock,note,status);
        verifyNoMoreInteractions(wrapperConnectorMock);
    }

    @Test
    void updateWrapperStationDetailsByOpt(){
        //given
        String note ="note";
        String status="TO_CHECK";
        StationDetails stationDetailsMock = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = new DummyWrapperEntity<>(stationDetailsMock);
        DummyWrapperEntities<StationDetails> wrapperEntities = new DummyWrapperEntities<>(wrapperEntity);

        when(wrapperConnectorMock.updateByOpt((StationDetails) any(),anyString(),anyString()))
                .thenReturn(wrapperEntities);
        //when
        WrapperEntitiesOperations response = wrapperService.updateWrapperStationDetailsByOpt(stationDetailsMock,note,status);
        //then
        assertNotNull(response);
        assertEquals(((DummyWrapperEntity) response.getWrapperEntityOperationsSortedList().get(0)).getEntity(), stationDetailsMock);
        reflectionEqualsByName(wrapperEntities.getWrapperEntityOperationsSortedList().get(0).getEntity(), stationDetailsMock);
        verify(wrapperConnectorMock, times(1))
                .updateByOpt(stationDetailsMock,note,status);
        verifyNoMoreInteractions(wrapperConnectorMock);
    }
}
