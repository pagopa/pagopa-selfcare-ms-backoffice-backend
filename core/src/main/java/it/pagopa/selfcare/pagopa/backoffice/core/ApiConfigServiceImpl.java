package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiConfigConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannelPaymentTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void deleteChannel(String channelCode, String xRequestId) {
        log.trace("deleteChannel start");
        apiConfigConnector.deleteChannel(channelCode, xRequestId);
        log.debug("deleteChannel with channelCode = {}", channelCode);
        log.trace("deleteChannel end");
    }

    @Override
    public void deleteAllChannelPaymentTypes(String channelCode, String xRequestId) {
        log.trace("deleteChannelPaymentTypeList start");
        PspChannelPaymentTypes paymentTypes = apiConfigConnector.getChannelPaymentTypes(channelCode,xRequestId);
        List<String> paymentTypeList = paymentTypes.getPaymentTypeList();
        paymentTypeList.forEach(paymentType->{
            apiConfigConnector.deleteChannelPaymentType(channelCode, paymentType, xRequestId);
        });
        log.trace("deleteChannelPaymentTypeList end");
    }
}
