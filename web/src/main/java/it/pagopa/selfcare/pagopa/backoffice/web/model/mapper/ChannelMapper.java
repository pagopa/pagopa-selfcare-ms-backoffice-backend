package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.web.model.Channels.ChannelResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.Channels.ChannelsResource;

import java.util.ArrayList;
import java.util.List;

public class ChannelMapper {

    public static ChannelResource toResource(Channel model) {
        ChannelResource resource = null;
        if (model != null) {
            resource = new ChannelResource();
            resource.setChannelCode(model.getChannelCode());
            resource.setEnabled(model.getEnabled());
            resource.setBrokerDescription(model.getBrokerDescription());
        }
        return resource;
    }

    public static ChannelsResource toResource(Channels model) {
        ChannelsResource resource = null;
        if (model != null) {
            resource = new ChannelsResource();
            List<ChannelResource> channelResourceList = new ArrayList<>();
            for (Channel c : model.getChannelList()) {
                channelResourceList.add(toResource(c));
            }
            resource.setChannelList(channelResourceList);
            resource.setPageInfo(model.getPageInfo());
        }
        return resource;
    }
}
