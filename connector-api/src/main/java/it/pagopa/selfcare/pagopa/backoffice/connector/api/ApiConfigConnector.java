package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
 import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannels;

public interface ApiConfigConnector {

    Channels getChannels(Integer limit, Integer page, String code, String sort, String xRequestId);

    ChannelDetails createChannel(ChannelDetails channelDetails, String xRequestId);
    PspChannels getPspChannels(String pspCode, String xRequestId);
    ChannelDetails getChannelDetails(String channelCode, String xRequestId);
}
