package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigSelfcareIntegrationConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAssociatedCodeList;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetailsList;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApiConfigSelfcareIntegrationServiceImpl.class)
class ApiConfigSelfcareIntegrationServiceImplTest {

    @Autowired
    private ApiConfigSelfcareIntegrationServiceImpl apiConfigSelfcareIntegrationService;

    @MockBean
    private ApiConfigSelfcareIntegrationConnector apiConfigConnectorMock;

    @Test
    void getStationsDetailsListByBroker_nullPage() {
        //given
        final Integer limit = 1;
        final Integer page = 0;
        final String broker = "broker";
        final String station = "station";
        final String xRequestId = "xRequestId";

        StationDetailsList stationDetailsListMock = mockInstance(new StationDetailsList());
        StationDetails stationDetailsMock = mockInstance(new StationDetails());
        stationDetailsListMock.setStationsDetailsList(List.of(stationDetailsMock));

        when(apiConfigConnectorMock.getStationsDetailsListByBroker(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(stationDetailsListMock);

        //when
        StationDetailsList response = apiConfigSelfcareIntegrationService.getStationsDetailsListByBroker(broker, station, limit, page);
        //then
        assertNotNull(response);
        assertEquals(response, stationDetailsListMock);
        reflectionEqualsByName(response, stationDetailsListMock);
        verify(apiConfigConnectorMock, times(1))
                .getStationsDetailsListByBroker(broker, station, limit, page);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getChannelDetailsListByBroker_nullPage() {
        //given
        final Integer limit = 1;
        final Integer page = 0;
        final String broker = "broker";
        final String station = "station";
        final String xRequestId = "xRequestId";

        ChannelDetailsList channelDetailsListMock = mockInstance(new ChannelDetailsList());
        ChannelDetails channelDetailsMock = mockInstance(new ChannelDetails());
        channelDetailsListMock.setChannelDetailsList(List.of(channelDetailsMock));

        when(apiConfigConnectorMock.getChannelDetailsListByBroker(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(channelDetailsListMock);

        //when
        ChannelDetailsList response = apiConfigSelfcareIntegrationService.getChannelsDetailsListByBroker(broker, station, limit, page);
        //then
        assertNotNull(response);
        assertEquals(response, channelDetailsListMock);
        reflectionEqualsByName(response, channelDetailsListMock);
        verify(apiConfigConnectorMock, times(1))
                .getChannelDetailsListByBroker(broker, station, limit, page);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getCreditorInstitutionSegregationcodes() {
        //given
        String ecCode = "ecCode";
        String xRequestId = "xRequestId";
        CreditorInstitutionAssociatedCodeList creditorInstitutionAssociatedCodeList = mockInstance(new CreditorInstitutionAssociatedCodeList());

        when(apiConfigConnectorMock.getCreditorInstitutionSegregationcodes(anyString()))
                .thenReturn(creditorInstitutionAssociatedCodeList);

        CreditorInstitutionAssociatedCodeList response = apiConfigSelfcareIntegrationService.getCreditorInstitutionSegregationcodes(ecCode);
        //then
        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1))
                .getCreditorInstitutionSegregationcodes(ecCode);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }
}
