package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannelPaymentTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode, String xRequestId) {
        log.trace("createChannelPaymentType start");
        PspChannelPaymentTypes response = apiConfigConnector.createChannelPaymentType(pspChannelPaymentTypes,channelCode, xRequestId);
        log.debug("createChannelPaymentType result = {}", response);
        log.trace("createChannelPaymentType end");
        return response;
    }

}
