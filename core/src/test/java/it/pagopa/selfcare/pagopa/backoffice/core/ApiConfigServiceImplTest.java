package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionAddress;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static it.pagopa.selfcare.pagopa.TestUtils.reflectionEqualsByName;
import static it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigServiceImpl.CREDITOR_INSTITUTION_CODE_IS_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApiConfigServiceImpl.class)
class ApiConfigServiceImplTest {

    @Autowired
    private ApiConfigServiceImpl apiConfigService;

    @MockBean
    private ApiConfigConnector apiConfigConnectorMock;

    @Test
    void getChannels_nullPage() {
        //given
        final Integer limit = 1;
        final Integer page = null;
        final String code = "code";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        //when
        apiConfigService.getChannels(limit, page, code, sort, xRequestId);
        //then
        verify(apiConfigConnectorMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getChannels() {
        //given
        final Integer limit = 1;
        final Integer page = 1;
        final String code = "code";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        Channels channelsMock = mock(Channels.class);
        when(apiConfigConnectorMock.getChannels(any(), any(), any(), any(), any()))
                .thenReturn(channelsMock);
        //when
        Channels channels = apiConfigService.getChannels(limit, page, code, sort, xRequestId);
        //then
        assertNotNull(channels);
        assertEquals(channelsMock, channels);
        reflectionEqualsByName(channelsMock, channels);
        verify(apiConfigConnectorMock, times(1))
                .getChannels(limit, page, code, sort, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getPspChannel() {
        //given

        final String pspcode = "pspcode";
        final String xRequestId = "xRequestId";
        PspChannels pspChannels = mock(PspChannels.class);
        when(apiConfigConnectorMock.getPspChannels(any(), any()))
                .thenReturn(pspChannels);
        //when
        PspChannels response = apiConfigService.getPspChannels(pspcode, xRequestId);
        //then
        assertNotNull(response);
        assertEquals(pspChannels, response);
        reflectionEqualsByName(pspChannels, response);
        verify(apiConfigConnectorMock, times(1))
                .getPspChannels(pspcode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void createChannel() {
        //given

        final String xRequestId = "xRequestId";
        ChannelDetails channelDetailsMock = mock(ChannelDetails.class);

        when(apiConfigConnectorMock.createChannel(any(), any()))
                .thenReturn(channelDetailsMock);
        //when
        ChannelDetails channelDetailsRes = apiConfigService.createChannel(channelDetailsMock, xRequestId);
        //then
        assertNotNull(channelDetailsRes);
        assertEquals(channelDetailsRes, channelDetailsMock);
        reflectionEqualsByName(channelDetailsRes, channelDetailsMock);
        verify(apiConfigConnectorMock, times(1))
                .createChannel(channelDetailsMock, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getChannelDetails() {
        //given

        final String channelCode = "channelCode";
        final String xRequestId = "xRequestId";
        ChannelDetails channelDetailsMock = mock(ChannelDetails.class);


        when(apiConfigConnectorMock.getChannelDetails(any(), any()))
                .thenReturn(channelDetailsMock);
        //when
        ChannelDetails channelDetailsRes = apiConfigService.getChannelDetails(channelCode, xRequestId);
        //then
        assertNotNull(channelDetailsRes);
        assertEquals(channelDetailsRes, channelDetailsMock);
        reflectionEqualsByName(channelDetailsRes, channelDetailsMock);
        verify(apiConfigConnectorMock, times(1))
                .getChannelDetails(channelCode, xRequestId);

        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void UpdateChannel() {
        //given
        final String xRequestId = "xRequestId";
        ChannelDetails channelDetailsMock = mock(ChannelDetails.class);
        String channelCode = "channelCode";

        when(apiConfigConnectorMock.updateChannel(any(), anyString(), anyString()))
                .thenReturn(channelDetailsMock);
        //when
        ChannelDetails channelDetailsRes = apiConfigService.updateChannel(channelDetailsMock, channelCode, xRequestId);
        //then
        assertNotNull(channelDetailsRes);
        assertEquals(channelDetailsRes, channelDetailsMock);
        reflectionEqualsByName(channelDetailsRes, channelDetailsMock);
        verify(apiConfigConnectorMock, times(1))
                .updateChannel(channelDetailsMock, channelCode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void createChannelPaymentType() {
        //given
        final String xRequestId = "xRequestId";
        final String channelCode = "channelCode";

        PspChannelPaymentTypes pspChannelPaymentTypesMock = mock(PspChannelPaymentTypes.class);
        pspChannelPaymentTypesMock.setPaymentTypeList(List.of("paymentType"));
        when(apiConfigConnectorMock.createChannelPaymentType(any(), anyString(), anyString()))
                .thenReturn(pspChannelPaymentTypesMock);
        //when
        PspChannelPaymentTypes pspChannelPaymentTypesRes = apiConfigService.createChannelPaymentType(pspChannelPaymentTypesMock, channelCode, xRequestId);
        //then
        assertNotNull(pspChannelPaymentTypesRes);
        assertEquals(pspChannelPaymentTypesRes, pspChannelPaymentTypesMock);
        reflectionEqualsByName(pspChannelPaymentTypesRes, pspChannelPaymentTypesMock);
        verify(apiConfigConnectorMock, times(1))
                .createChannelPaymentType(pspChannelPaymentTypesMock, channelCode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getPaymentTypes() {
        //given
        final String xRequestId = "xRequestId";

        PaymentTypes paymentTypes = mock(PaymentTypes.class);
        when(apiConfigConnectorMock.getPaymentTypes(anyString()))
                .thenReturn(paymentTypes);

        //when
        PaymentTypes paymentTypesResp = apiConfigService.getPaymentTypes(xRequestId);
        //then
        assertNotNull(paymentTypesResp);
        assertEquals(paymentTypesResp, paymentTypes);

        verify(apiConfigConnectorMock, times(1))
                .getPaymentTypes(anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }


    @Test
    void getChannelPaymentTypes() {
        //given
        final String xRequestId = "xRequestId";
        final String channelCode = "channelCode";

        PspChannelPaymentTypes pspChannelPaymentTypes = mock(PspChannelPaymentTypes.class);
        when(apiConfigConnectorMock.getChannelPaymentTypes(anyString(), anyString()))
                .thenReturn(pspChannelPaymentTypes);

        //when
        PspChannelPaymentTypes pspChannelPaymentTypesResp = apiConfigService.getChannelPaymentTypes(channelCode, xRequestId);
        //then
        assertNotNull(pspChannelPaymentTypesResp);
        assertEquals(pspChannelPaymentTypesResp, pspChannelPaymentTypes);

        verify(apiConfigConnectorMock, times(1))
                .getChannelPaymentTypes(anyString(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void deleteChannelPaymentType() {
        //given
        final String xRequestId = "xRequestId";
        final String channelCode = "channelCode";
        final String paymentType = "paymentType";

        doNothing().when(apiConfigConnectorMock).deleteChannelPaymentType(anyString(), anyString(), anyString());

        //when
        apiConfigService.deleteChannelPaymentType(channelCode, paymentType, xRequestId);
        //then
        verify(apiConfigConnectorMock, times(1))
                .deleteChannelPaymentType(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void deletePaymentServiceProvidersChannels() {
        final String xRequestId = "xRequestId";
        final String channelCode = "channelCode";
        final String pspCode = "pspCode";

        doNothing().when(apiConfigConnectorMock).deletePaymentServiceProvidersChannels(anyString(), anyString(), anyString());

        //when
        apiConfigService.deletePaymentServiceProvidersChannels(channelCode, pspCode, xRequestId);
        //then
        verify(apiConfigConnectorMock, times(1))
                .deletePaymentServiceProvidersChannels(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void updatePaymentServiceProvidersChannels() {
        final String xRequestId = "xRequestId";
        final String pspCode = "pspCode";
        final String channelCode = "channelCode";

        PspChannelPaymentTypes pspChannelPaymentTypesMock = mockInstance(new PspChannelPaymentTypes());
        pspChannelPaymentTypesMock.setPaymentTypeList(List.of("paymentType"));

        when(apiConfigConnectorMock.updatePaymentServiceProvidersChannels(anyString(), anyString(), any(), anyString()))
                .thenReturn(pspChannelPaymentTypesMock);
        //when
        PspChannelPaymentTypes response = apiConfigService.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypesMock, xRequestId);
        //then
        assertNotNull(response);
        assertEquals(pspChannelPaymentTypesMock, response);
        reflectionEqualsByName(pspChannelPaymentTypesMock, response);
        verify(apiConfigConnectorMock, times(1))
                .updatePaymentServiceProvidersChannels(anyString(), anyString(), any(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void deleteChannel() {
        //given
        final String xRequestId = "xRequestId";
        final String channelCode = "channelCode";

        doNothing().when(apiConfigConnectorMock).deleteChannel(anyString(), anyString());

        //when
        apiConfigService.deleteChannel(channelCode, xRequestId);
        //then

        verify(apiConfigConnectorMock, times(1))
                .deleteChannel(channelCode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getPspBrokerPsp() {
        //given
        final String xRequestId = "xRequestId";
        final String brokerPspCode = "brokerPspCode";
        final Integer limit = 1;
        final Integer page = 1;

        PaymentServiceProviders modelMock = mock(PaymentServiceProviders.class);
        when(apiConfigConnectorMock.getPspBrokerPsp(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(modelMock);

        //when
        PaymentServiceProviders response = apiConfigService.getPspBrokerPsp(limit, page, brokerPspCode, xRequestId);
        //then
        assertNotNull(response);
        assertEquals(response, modelMock);

        verify(apiConfigConnectorMock, times(1))
                .getPspBrokerPsp(anyInt(), anyInt(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }


    @Test
    void getStations() {
        //given
        final Integer limit = 1;
        final Integer page = 1;
        final String ecCode = "ecCode";
        final String stationCode = "stationCode";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        Stations stationsMock = mockInstance(new Stations());
        when(apiConfigConnectorMock.getStations(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(stationsMock);

        //when
        Stations stations = apiConfigService.getStations(limit, page, sort,null,  ecCode, stationCode, xRequestId);

        //then
        assertNotNull(stations);
        assertEquals(stationsMock, stations);
        reflectionEqualsByName(stationsMock, stations);
        verify(apiConfigConnectorMock, times(1))
                .getStations(limit, page, sort, null, ecCode, stationCode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getStations_Exception() {
        //given
        final Integer limit = 1;
        final Integer page = 1;
        final String ecCode = "ecCode";
        final String stationCode = "stationCode";
        final String sort = "sort";
        final String xRequestId = "xRequestId";
        Stations stationsMock = mockInstance(new Stations());

        when(apiConfigConnectorMock.getStations(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("[404 Not Found]"));
        //when
        Stations stations = apiConfigService.getStations(limit, page, sort,null,  ecCode, stationCode, xRequestId);

        //then
        assertNotNull(stations);
        verify(apiConfigConnectorMock, times(1))
                .getStations(limit, page, sort, null, ecCode, stationCode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getStation() {
        //given
        final String stationCode = "stationCode";
        final String xRequestId = "xRequestId";
        StationDetails stationDetailsMock = mock(StationDetails.class);
        when(apiConfigConnectorMock.getStation(any(), any()))
                .thenReturn(stationDetailsMock);

        //when
        StationDetails stationDetails = apiConfigService.getStation(stationCode, xRequestId);

        //then
        assertNotNull(stationDetails);
        assertEquals(stationDetailsMock, stationDetails);
        reflectionEqualsByName(stationDetailsMock, stationDetails);
        verify(apiConfigConnectorMock, times(1))
                .getStation(stationCode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void createStation() {
        //given
        final String xRequestId = "xRequestId";
        StationDetails stationDetails = mockInstance(new StationDetails());

        when(apiConfigConnectorMock.createStation(any(), anyString()))
                .thenReturn(stationDetails);
        //when
        StationDetails response = apiConfigService.createStation(stationDetails, xRequestId);
        //then
        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1)).createStation(stationDetails, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
        assertEquals(response, stationDetails);
    }

    @Test
    void getChannelsCSV() throws IOException {
        //given
        final String xRequestId = "xRequestId";

        File file = File.createTempFile("channels", ".csv");
        FileWriter writer = new FileWriter(file);
        writer.write("id,name\n1,channel1\n2,channel2\n");
        writer.close();
        Resource resource = mockInstance(new FileSystemResource(file));

        when(apiConfigConnectorMock.getChannelsCSV(anyString()))
                .thenReturn(resource);

        //when
        Resource resourceResp = apiConfigService.getChannelsCSV(xRequestId);
        //then
        assertNotNull(resourceResp);
        assertEquals(resourceResp, resource);

        verify(apiConfigConnectorMock, times(1))
                .getChannelsCSV(anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getChannelPaymentServiceProviders() {
        //given
        final String xRequestId = "xRequestId";
        final String channelCode = "channelCode";
        final Integer limit = 1;
        final Integer page = 1;

        ChannelPspList channelPspListMock = mockInstance(new ChannelPspList());
        ChannelPsp channelPsp = mock(ChannelPsp.class);
        channelPspListMock.setPsp(List.of(channelPsp));
        when(apiConfigConnectorMock.getChannelPaymentServiceProviders(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(channelPspListMock);

        //when
        ChannelPspList response = apiConfigService.getChannelPaymentServiceProviders(limit, page, channelCode, xRequestId);
        //then
        assertNotNull(response);
        assertFalse(response.getPsp().isEmpty());
        assertEquals(response, channelPspListMock);

        verify(apiConfigConnectorMock, times(1))
                .getChannelPaymentServiceProviders(anyInt(), anyInt(), anyString(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void createBrokerPsp() {
        //given
        final String xRequestId = "xRequestId";
        BrokerPspDetails brokerPspDetails = mockInstance(new BrokerPspDetails());

        when(apiConfigConnectorMock.createBrokerPsp(any(), anyString()))
                .thenReturn(brokerPspDetails);

        //when
        BrokerPspDetails response = apiConfigService.createBrokerPsp(brokerPspDetails, xRequestId);
        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1)).createBrokerPsp(brokerPspDetails, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
        assertEquals(response, brokerPspDetails);
    }


    @Test
    void createPaymentServiceProvider() {
        //given
        final String xRequestId = "xRequestId";
        PaymentServiceProviderDetails paymentServiceProviderDetails = mockInstance(new PaymentServiceProviderDetails());

        when(apiConfigConnectorMock.createPaymentServiceProvider(any(), anyString()))
                .thenReturn(paymentServiceProviderDetails);

        //when
        PaymentServiceProviderDetails response = apiConfigService.createPaymentServiceProvider(paymentServiceProviderDetails, xRequestId);
        assertNotNull(response);

        assertEquals(response, paymentServiceProviderDetails);
    }

    @Test
    void generateChannelCode() {
        //given
        final String xRequestId = "xRequestId";
        final String pspCode = "pspCode";

        PspChannels pspChannels = mockInstance(new PspChannels());
        PspChannel pspChannel = mockInstance(new PspChannel());
        pspChannel.setChannelCode("TEST_01");

        pspChannels.setChannelsList(List.of(pspChannel));

        when(apiConfigConnectorMock.getPspChannels(any(), anyString()))
                .thenReturn(pspChannels);

        //when
        String response = apiConfigService.generateChannelCode(anyString(), anyString());
        assertNotNull(response);

        assertEquals("TEST_02", response);
    }

    @Test
    void generateChannelCode_noRegexMatcher() {
        //given
        final String xRequestId = "xRequestId";
        final String pspCode = "TEST";

        PspChannels pspChannels = mockInstance(new PspChannels());
        PspChannel pspChannel = mockInstance(new PspChannel());
        pspChannel.setChannelCode("TEST");

        pspChannels.setChannelsList(List.of(pspChannel));

        when(apiConfigConnectorMock.getPspChannels(any(), anyString()))
                .thenReturn(pspChannels);

        //when
        String response = apiConfigService.generateChannelCode(pspCode, xRequestId);
        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1))
                .getPspChannels(anyString(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
        assertEquals("TEST_01", response);
    }

    @Test
    void generateStationCode() {
        //given
        final String xRequestId = "xRequestId";
        final Integer limit = 100;
        final Integer page = 0;
        final String sort ="ASC";
        final String brokerCode=null;
        final String ecCode = null;
        final String stationCode = "stationCode";

        Stations s =  mockInstance(new Stations());
        Station station=  mockInstance(new Station());
        station.setStationCode(stationCode+"_01");
        s.setStationsList(List.of(station));

        when(apiConfigConnectorMock.getStations(limit,page,sort,brokerCode,ecCode,stationCode,xRequestId))
                .thenReturn(s);

        //when
        String response = apiConfigService.generateStationCode(stationCode, xRequestId);
        //then
        assertNotNull(response);
        assertEquals(stationCode+"_02", response);
        verify(apiConfigConnectorMock, times(1))
                .getStations(limit,page,sort,brokerCode,ecCode, stationCode,xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void updateCreditorInstitution_nullEcCode() {
        //given
        String ecCode = null;
        String xRequestId = UUID.randomUUID().toString();
        CreditorInstitutionDetails request = mockInstance(new CreditorInstitutionDetails());
        //when
        Executable executable = () -> apiConfigService.updateCreditorInstitutionDetails(ecCode, request, xRequestId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(CREDITOR_INSTITUTION_CODE_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(apiConfigConnectorMock);
    }

    @Test
    void updateCreditorInstitution() {
        //given
        String ecCode = "ecCode";
        String xRequestId = UUID.randomUUID().toString();
        CreditorInstitutionDetails request = mockInstance(new CreditorInstitutionDetails());
        when(apiConfigConnectorMock.updateCreditorInstitutionDetails(anyString(), any(), anyString()))
                .thenReturn(request);
        //when
        CreditorInstitutionDetails response = apiConfigService.updateCreditorInstitutionDetails(ecCode, request, xRequestId);
        //then
        assertSame(request, response);
        verify(apiConfigConnectorMock, times(1)).updateCreditorInstitutionDetails(ecCode, request, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void generateStationCode_noRegexMatcher() {
        //given
        final String xRequestId = "xRequestId";
        final Integer limit = 100;
        final Integer page = 0;
        final String sort ="ASC";
        final String brokerCode=null;
        final String ecCode = null;
        final String stationCode = "stationCode";

        Stations s =  mockInstance(new Stations());
        s.setStationsList(new ArrayList<>());

        when(apiConfigConnectorMock.getStations(limit,page,sort,brokerCode,ecCode,stationCode,xRequestId))
                .thenReturn(s);

        //when
        String response = apiConfigService.generateStationCode(stationCode, xRequestId);
        //then
        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1))
                .getStations(limit,page,sort,brokerCode,ecCode, stationCode,xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
        assertEquals("stationCode_01", response);
    }

    @Test
    void getPSPDetails() {
        //given
        final String pspCode = "pspCode";
        final String xRequestId = "xRequestId";

        PaymentServiceProviderDetails paymentServiceProviderDetails = mockInstance(new PaymentServiceProviderDetails());

        when(apiConfigConnectorMock.getPSPDetails(anyString(), anyString()))
                .thenReturn(paymentServiceProviderDetails);
        //when
        PaymentServiceProviderDetails response = apiConfigService.getPSPDetails(pspCode, xRequestId);

        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1))
                .getPSPDetails(anyString(), anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);
        assertEquals(response, paymentServiceProviderDetails);
    }

    @Test
    void createCreditorInstitutionStationRelationship() {
        //given
        String ecCode = "ecCode";
        String xRequestId = "xRequestId";
        CreditorInstitutionStationEdit station = mockInstance(new CreditorInstitutionStationEdit());
        CreditorInstitutionStationEdit responseMock = mockInstance(new CreditorInstitutionStationEdit());
        when(apiConfigConnectorMock.createCreditorInstitutionStationRelationship(anyString(), any(), anyString()))
                .thenReturn(responseMock);
        //when
        CreditorInstitutionStationEdit response = apiConfigService.createCreditorInstitutionStationRelation(ecCode, station, xRequestId);
        //then
        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1))
                .createCreditorInstitutionStationRelationship(ecCode, station, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void createCreditorInstitution() {
        //given
        String xRequestId = UUID.randomUUID().toString();
        CreditorInstitutionDetails creditorInstitutionDetails = mockInstance(new CreditorInstitutionDetails());
        when(apiConfigConnectorMock.createCreditorInstitution(any(), anyString()))
                .thenReturn(creditorInstitutionDetails);
        //when
        CreditorInstitutionDetails result = apiConfigService.createCreditorInstitution(creditorInstitutionDetails, xRequestId);
        //then
        assertNotNull(result);
        assertEquals(creditorInstitutionDetails, result);
        verify(apiConfigConnectorMock, times(1))
                .createCreditorInstitution(creditorInstitutionDetails, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void getCreditorInstitutionDetails_nullCode() {
        //given
        String ecCode = null;
        String xRequestId = UUID.randomUUID().toString();
        //when
        Executable executable = () -> apiConfigService.getCreditorInstitutionDetails(ecCode, xRequestId);
        //then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(CREDITOR_INSTITUTION_CODE_IS_REQUIRED, e.getMessage());
        verifyNoInteractions(apiConfigConnectorMock);
    }

    @Test
    void getCreditorInstitutionDetails() {
        //given
        String ecCode = "creditorInstitutionCode";
        String xRequestId = UUID.randomUUID().toString();

        CreditorInstitutionDetails creditorInstitutionDetails = mockInstance(new CreditorInstitutionDetails());
        CreditorInstitutionAddress address = mockInstance(new CreditorInstitutionAddress());
        creditorInstitutionDetails.setAddress(address);
        when(apiConfigConnectorMock.getCreditorInstitutionDetails(anyString(), anyString()))
                .thenReturn(creditorInstitutionDetails);
        //when
        CreditorInstitutionDetails result = apiConfigService.getCreditorInstitutionDetails(ecCode, xRequestId);
        //then
        assertSame(creditorInstitutionDetails, result);
        verify(apiConfigConnectorMock, times(1))
                .getCreditorInstitutionDetails(ecCode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void updateStation() {
        //given
        final String xRequestId = "xRequestId";
        StationDetails stationDetailsMock = mock(StationDetails.class);
        String stationCode = "stationCode";

        when(apiConfigConnectorMock.updateStation(anyString(), any(), anyString()))
                .thenReturn(stationDetailsMock);
        //when
        StationDetails stationDetailsRes = apiConfigService.updateStation(stationCode, stationDetailsMock, xRequestId);
        //then
        assertNotNull(stationDetailsRes);
        assertEquals(stationDetailsRes, stationDetailsMock);
        reflectionEqualsByName(stationDetailsRes, stationDetailsMock);
        verify(apiConfigConnectorMock, times(1))
                .updateStation(stationCode, stationDetailsMock, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void mergeAndSortWrapperStations_ASC() {
        //given
        WrapperStations stations = mock(WrapperStations.class);
        String sorting = "ASC";

        //when
        WrapperStations stationsRes = apiConfigService.mergeAndSortWrapperStations(stations, stations, sorting);
        //then
        assertNotNull(stationsRes);
    }

    @Test
    void mergeAndSortWrapperChannels_ASC() {
        //given
        WrapperChannels channels = mock(WrapperChannels.class);
        String sorting = "ASC";

        //when
        WrapperChannels channelsRes = apiConfigService.mergeAndSortWrapperChannels(channels, channels, sorting);
        //then
        assertNotNull(channelsRes);
    }
    @Test
    void mergeAndSortWrapperStations_DESC() {
        //given
        WrapperStations stations = mock(WrapperStations.class);
        String sorting = "DESC";

        //when
        WrapperStations stationsRes = apiConfigService.mergeAndSortWrapperStations(stations, stations,sorting);
        //then
        assertNotNull(stationsRes);
    }

    @Test
    void mergeAndSortWrapperChannels_DESC() {
        //given
        WrapperChannels channels = mock(WrapperChannels.class);
        String sorting = "DESC";

        //when
        WrapperChannels channelsRes = apiConfigService.mergeAndSortWrapperChannels(channels, channels,sorting);
        //then
        assertNotNull(channelsRes);
    }
    @Test
    void mergeAndSortWrapperStations_nullSorting() {
        //given
        WrapperStations stations = mock(WrapperStations.class);
        String sorting = null;

        //when
        WrapperStations stationsRes = apiConfigService.mergeAndSortWrapperStations(stations, stations,sorting);
        //then
        assertNotNull(stationsRes);
    }

    @Test
    void mergeAndSortWrapperChannels_nullSorting() {
        //given
        WrapperChannels channels = mock(WrapperChannels.class);
        String sorting = null;

        //when
        WrapperChannels channelsRes = apiConfigService.mergeAndSortWrapperChannels(channels, channels,sorting);
        //then
        assertNotNull(channelsRes);
    }

    @Test
    void getWfespPlugins() {
        //given
        final String xRequestId = "xRequestId";
        WfespPluginConfs wfespPluginConfsMock = mock(WfespPluginConfs.class);

        when(apiConfigConnectorMock.getWfespPlugins(anyString()))
                .thenReturn(wfespPluginConfsMock);

        //when
        WfespPluginConfs response = apiConfigService.getWfespPlugins(xRequestId);
        //then
        assertNotNull(response);
        verify(apiConfigConnectorMock, times(1))
                .getWfespPlugins(anyString());
        verifyNoMoreInteractions(apiConfigConnectorMock);

    }

    @Test
    void getCreditorInstitutionsByStation() {
        //given
        String stationCode = "stationCode";
        String xRequestId = "xRequestId";
        Integer page = 0;

        CreditorInstitution creditorInstitution = mockInstance(new CreditorInstitution());
        CreditorInstitutions creditorInstitutions = mockInstance(new CreditorInstitutions());
        List<CreditorInstitution> creditorInstitutionList = new ArrayList<>();
        creditorInstitutionList.add(creditorInstitution);
        creditorInstitutions.setCreditorInstitutionList(creditorInstitutionList);

        when(apiConfigConnectorMock.getCreditorInstitutionsByStation(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(creditorInstitutions);
        //when
        CreditorInstitutions result = apiConfigService.getCreditorInstitutionsByStation(stationCode,50,0, xRequestId);
        //then
        assertSame(creditorInstitutions, result);
        verify(apiConfigConnectorMock, times(1))
                .getCreditorInstitutionsByStation(stationCode,50,0, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);
    }

    @Test
    void deleteCreditorInstitutionStationRelationship(){
        //given
        String ecCode = "ecCode";
        String xRequestId = "1";
        String stationcode = "stationcode";

        doNothing().when(apiConfigConnectorMock).deleteCreditorInstitutionStationRelationship(anyString(), anyString(), anyString());

        //when
        apiConfigService.deleteCreditorInstitutionStationRelationship(ecCode, stationcode, xRequestId);
        //then

        verify(apiConfigConnectorMock, times(1))
                .deleteCreditorInstitutionStationRelationship(ecCode, stationcode, xRequestId);
        verifyNoMoreInteractions(apiConfigConnectorMock);

    }
}
