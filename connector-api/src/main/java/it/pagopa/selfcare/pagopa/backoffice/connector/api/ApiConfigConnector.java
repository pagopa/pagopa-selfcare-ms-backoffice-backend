package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannels;

public interface ApiConfigConnector {

    Channels getChannels(Integer limit, Integer page, String code, String sort, String xRequestId);

    ChannelDetails createChannel(ChannelDetails channelDetails, String xRequestId);
    ChannelDetails updateChannel(ChannelDetails channelDetails,String channelCode, String xRequestId);
    PspChannels getPspChannels(String pspCode, String xRequestId);
    void deleteChannelPaymentType(String channelCode, String paymentTypeCode, String xRequestId);
    PspChannelPaymentTypes getChannelPaymentTypes(String channelCode, String xRequestId);
    ChannelDetails getChannelDetails(String channelCode, String xRequestId);
    PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode, String xRequestId);
    PaymentTypes getPaymentTypes(String xRequestId);
    void deletePaymentServiceProvidersChannels(String channelCode,String pspCode,String xRequestId);
    void deleteChannel(String channelCode, String xRequestId);
}
