package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannelPaymentTypes;

import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.*;

import java.util.ArrayList;
import java.util.List;

public class ChannelMapper {

    private ChannelMapper() {
    }

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

    public static ChannelDetailsResource toResource(ChannelDetails model, PspChannelPaymentTypes listModel) {
        ChannelDetailsResource resource = null;
        if (model != null) {
            resource = new ChannelDetailsResource();
            resource.setPassword(model.getPassword());
            resource.setNewPassword(model.getNewPassword());
            resource.setProtocol(model.getProtocol());
            resource.setIp(model.getIp());
            resource.setPort(model.getPort());
            resource.setService(model.getService());
            resource.setBrokerPspCode(model.getBrokerPspCode());
            resource.setProxyEnabled(model.getProxyEnabled());
            resource.setProxyHost(model.getProxyHost());
            resource.setProxyPort(model.getProxyPort());
            resource.setProxyUsername(model.getProxyUsername());
            resource.setProxyPassword(model.getProxyPassword());
            resource.setTargetHost(model.getTargetHost());
            resource.setTargetPort(model.getTargetPort());
            resource.setTargetPath(model.getTargetPath());
            resource.setThreadNumber(model.getThreadNumber());
            resource.setTimeoutA(model.getTimeoutA());
            resource.setTimeoutB(model.getTimeoutB());
            resource.setTimeoutC(model.getTimeoutC());
            resource.setNpmService(model.getNpmService());
            resource.setNewFaultCode(model.getNewFaultCode());
            resource.setRedirectIp(model.getRedirectIp());
            resource.setRedirectPath(model.getRedirectPath());
            resource.setRedirectPort(model.getRedirectPort());
            resource.setRedirectQueryString(model.getRedirectQueryString());
            resource.setRedirectProtocol(model.getRedirectProtocol());
            resource.setPaymentModel(model.getPaymentModel());
            resource.setServPlugin(model.getServPlugin());
            resource.setRtPush(model.getRtPush());
            resource.setOnUs(model.getOnUs());
            resource.setCardChart(model.getCardChart());
            resource.setRecovery(model.getRecovery());
            resource.setDigitalStampBrand(model.getDigitalStampBrand());
            resource.setFlagIo(model.getFlagIo());
            resource.setAgid(model.getAgid());
            resource.setBrokerDescription(model.getBrokerDescription());
            resource.setEnabled(model.getEnabled());
            resource.setChannelCode(model.getChannelCode());
            resource.setPrimitiveVersion(model.getPrimitiveVersion());
            resource.setPaymentTypeList(listModel!=null?listModel.getPaymentTypeList():new ArrayList<>());
        }
        return resource;
    }

    public static ChannelDetails fromChannelDetailsDto(ChannelDetailsDto model) {
        ChannelDetails resource = null;
        if (model != null) {
            resource = new ChannelDetails();
            resource.setPassword(model.getPassword());
            resource.setNewPassword(model.getNewPassword());
            resource.setProtocol(model.getProtocol());
            resource.setIp(model.getIp());
            resource.setPort(model.getPort());
            resource.setService(model.getService());
            resource.setBrokerPspCode(model.getBrokerPspCode());
            resource.setProxyEnabled(model.getProxyEnabled());
            resource.setProxyHost(model.getProxyHost());
            resource.setProxyPort(model.getProxyPort());
            resource.setProxyUsername(model.getProxyUsername());
            resource.setProxyPassword(model.getProxyPassword());
            resource.setTargetHost(model.getTargetHost());
            resource.setTargetPort(model.getTargetPort());
            resource.setTargetPath(model.getTargetPath());
            resource.setThreadNumber(model.getThreadNumber());
            resource.setTimeoutA(model.getTimeoutA());
            resource.setTimeoutB(model.getTimeoutB());
            resource.setTimeoutC(model.getTimeoutC());
            resource.setNpmService(model.getNpmService());
            resource.setNewFaultCode(model.getNewFaultCode());
            resource.setRedirectIp(model.getRedirectIp());
            resource.setRedirectPath(model.getRedirectPath());
            resource.setRedirectPort(model.getRedirectPort());
            resource.setRedirectQueryString(model.getRedirectQueryString());
            resource.setRedirectProtocol(model.getRedirectProtocol());
            resource.setPaymentModel(model.getPaymentModel());
            resource.setServPlugin(model.getServPlugin());
            resource.setRtPush(model.getRtPush());
            resource.setOnUs(model.getOnUs());
            resource.setCardChart(model.getCardChart());
            resource.setRecovery(model.getRecovery());
            resource.setDigitalStampBrand(model.getDigitalStampBrand());
            resource.setFlagIo(model.getFlagIo());
            resource.setAgid(model.getAgid());
            resource.setBrokerDescription(model.getBrokerDescription());
            resource.setEnabled(model.getEnabled());
            resource.setChannelCode(model.getChannelCode());
            resource.setPrimitiveVersion(model.getPrimitiveVersion());
        }
        return resource;
    }


    public static PspChannelResource toResource(PspChannel model){
        PspChannelResource resource = null;
        if (model != null) {
            resource = new PspChannelResource();
            resource.setChannelCode(model.getChannelCode());
            resource.setEnabled(model.getEnabled());
            resource.setPaymentTypeList(model.getPaymentTypeList());
        }
        return  resource;
    }

    public static PspChannelsResource toResource(PspChannels model){
        List<PspChannelResource> channelResourceList = new ArrayList<>();
        PspChannelsResource resource = null;
        if (model != null) {
            resource = new PspChannelsResource();
            List<PspChannel> channels = model.getChannelsList();
            channels.forEach(pspChannel->
                channelResourceList.add(toResource(pspChannel))
            );
            resource.setChannelsList(channelResourceList);
        }
        return  resource;
    }


    public static PspChannelPaymentTypesResource toResource(PspChannelPaymentTypes model) {
        PspChannelPaymentTypesResource resource = null;
        if (model != null) {
            resource = new PspChannelPaymentTypesResource();
            resource.setPaymentTypeList(model.getPaymentTypeList());
        }
        return resource;
    }

}
