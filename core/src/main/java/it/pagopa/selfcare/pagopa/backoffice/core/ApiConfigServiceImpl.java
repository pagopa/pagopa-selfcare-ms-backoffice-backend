package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetail;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetail;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiConfigServiceImpl implements ApiConfigService {

    private final ApiConfigConnector apiConfigConnector;

    @Autowired
    public ApiConfigServiceImpl(ApiConfigConnector apiConfigConnector) {
        this.apiConfigConnector = apiConfigConnector;
    }

    @Override
    public Channels getChannels(Integer limit, Integer page, String code, String sort, String xRequestId) {
        log.trace("getChannels start");
        Channels channels = apiConfigConnector.getChannels(limit, page, code, sort, xRequestId);
        log.debug("getChannels result = {}", channels);
        log.trace("getChannels end");
        return channels;
    }

    @Override
    public ChannelDetails createChannel(ChannelDetails channelDetails, String xRequestId) {
        log.trace("createChannel start");
        ChannelDetails response = apiConfigConnector.createChannel(channelDetails, xRequestId);
        log.debug("createChannel result = {}", response);
        log.trace("createChannel end");
        return response;
    }

    @Override
    public ChannelDetails updateChannel(ChannelDetails channelDetails, String channelCode, String xRequestId) {
        log.trace("updateChannel start");
        ChannelDetails response = apiConfigConnector.updateChannel(channelDetails, channelCode, xRequestId);
        log.debug("updateChannel result = {}", response);
        log.trace("updateChannel end");
        return response;
    }

    @Override
    public PspChannels getPspChannels(String pspCode, String xRequestId) {
        log.trace("getPspChannels start");
        PspChannels response = apiConfigConnector.getPspChannels(pspCode, xRequestId);
        log.debug("getPspChannels result = {}", response);
        log.trace("getPspChannels end");
        return response;
    }

    @Override
    public ChannelDetails getChannelDetails(String channelCode, String xRequestId) {
        log.trace("getChannelDetails start");
        ChannelDetails response = apiConfigConnector.getChannelDetails(channelCode, xRequestId);
        log.debug("getChannelDetails result = {}", response);
        log.trace("getChannelDetails end");
        return response;
    }

    public PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode, String xRequestId) {
        log.trace("createChannelPaymentType start");
        PspChannelPaymentTypes response = apiConfigConnector.createChannelPaymentType(pspChannelPaymentTypes, channelCode, xRequestId);
        log.debug("createChannelPaymentType result = {}", response);
        log.trace("createChannelPaymentType end");
        return response;
    }

    @Override
    public PaymentTypes getPaymentTypes(String xRequestId) {
        log.trace("getPaymentTypes start");
        PaymentTypes response = apiConfigConnector.getPaymentTypes(xRequestId);
        log.debug("getPaymentTypes result = {}", response);
        log.trace("getPaymentTypes end");
        return response;
    }

    @Override
    public void deleteChannelPaymentType(String channelCode, String paymenTtypeCode, String xRequestId) {
        log.trace("deletePaymentTypes start");
        apiConfigConnector.deleteChannelPaymentType(channelCode, paymenTtypeCode, xRequestId);
        log.trace("deletePaymentTypes end");
    }

    @Override
    public PspChannelPaymentTypes getChannelPaymentTypes(String channelCode, String xRequestId) {
        log.trace("getChannelPaymentTypes start");
        PspChannelPaymentTypes response = apiConfigConnector.getChannelPaymentTypes(channelCode, xRequestId);
        log.debug("getChannelPaymentTypes result = {}", response);
        log.trace("getChannelPaymentTypes end");
        return response;
    }

    @Override
    public void deletePaymentServiceProvidersChannels(String pspCode, String channelCode, String xRequestId) {
        log.trace("deletePaymentServiceProvidersChannels start");
        apiConfigConnector.deletePaymentServiceProvidersChannels(pspCode, channelCode, xRequestId);
        log.trace("deletePaymentServiceProvidersChannels end");
    }

    @Override
    public PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes, String xRequestId) {
        log.trace("updatePaymentServiceProvidersChannels start");
        PspChannelPaymentTypes response = apiConfigConnector.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypes, xRequestId);
        log.debug("updatePaymentServiceProvidersChannels result = {}", response);
        log.trace("updatePaymentServiceProvidersChannels end");
        return response;
    }

    @Override
    public void deleteChannel(String channelCode, String xRequestId) {
        log.trace("deleteChannel start");
        apiConfigConnector.deleteChannel(channelCode, xRequestId);
        log.debug("deleteChannel with channelCode = {}", channelCode);
        log.trace("deleteChannel end");
    }

    @Override
    public PaymentServiceProviders getPspBrokerPsp(Integer limit, Integer page, String brokerPspCode, String uuid) {
        log.trace("getPspBrokerPsp start");
        PaymentServiceProviders response = apiConfigConnector.getPspBrokerPsp(limit, page, brokerPspCode, uuid);
        log.debug("getPspBrokerPsp result = {}", response);
        log.trace("getPspBrokerPsp end");
        return response;
    }

    @Override
    public Stations getStations(Integer limit, Integer page, String sort, String ecCode, String stationCode, String xRequestId) {
        log.trace("getChannels start");
        log.debug("getChannels ecCode = {}, stationCode = {}, xRequestId = {}", ecCode, stationCode, xRequestId);
        Stations response = apiConfigConnector.getStations(limit, page, sort, null, ecCode, stationCode, xRequestId);
        log.debug("getChannels result = {}", response);
        log.trace("getChannels end");
        return response;
    }

    @Override
    public StationDetail getStation(String stationCode, String xRequestId) {
        log.trace("getChannel start");
        log.debug("getChannel stationCode = {}, xRequestId = {}", stationCode, xRequestId);
        StationDetail response = apiConfigConnector.getStation(stationCode, xRequestId);
        log.debug("getChannels result = {}", response);
        return response;
    }

    @Override
    public Resource getChannelsCSV(String uuid) {
        log.trace("getChannelsCSV start");
        Resource response = apiConfigConnector.getChannelsCSV(uuid);
        log.debug("getChannelsCSV result = {}", response);
        log.trace("getChannelsCSV end");
        return response;
    }

    public ChannelPspList getChannelPaymentServiceProviders(Integer limit, Integer page, String channelCode, String uuid) {
        log.trace("getChannelPaymentServiceProviders start");
        ChannelPspList response = apiConfigConnector.getChannelPaymentServiceProviders(limit, page, channelCode, uuid);
        log.debug("getChannelPaymentServiceProviders result = {}", response);
        log.trace("getChannelPaymentServiceProviders end");
        return response;
    }

    @Override
    public BrokerPspDetails createBrokerPsp(BrokerPspDetails brokerPspDetails, String xRequestId) {
        log.trace("createBrokerPsp start");
        BrokerPspDetails response = apiConfigConnector.createBrokerPsp(brokerPspDetails, xRequestId);
        log.debug("createBrokerPsp result = {}", response);
        log.trace("createBrokerPsp end");
        return response;
    }
}
