package it.pagopa.selfcare.pagopa.backoffice.mapper;

import it.pagopa.selfcare.pagopa.backoffice.entity.WrapperEntityOperations;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.service.WrapperService.getWrapperEntityOperationsSortedList;

public class ChannelMapper {

    private ChannelMapper() {
    }


    public static BrokersPspResource toResource(BrokersPsp model) {
        BrokersPspResource resource = null;
        List<BrokerPspResource> list = new ArrayList<>();
        if(model != null) {
            resource = new BrokersPspResource();
            resource.setPageInfo(model.getPageInfo());
            model.getBrokerPspList().forEach(i -> {
                list.add(toResource(i));
            });
            resource.setBrokerPspResources(list);


        }
        return resource;
    }

    public static BrokerPspResource toResource(BrokerPsp model) {
        BrokerPspResource resource = null;
        if(model != null) {
            resource = new BrokerPspResource();
            resource.setDescription(model.getDescription());
            resource.setEnabled(model.getEnabled());
            resource.setBrokerPspCode(model.getBrokerPspCode());

        }
        return resource;
    }

    public static ChannelResource toResource(Channel model) {
        ChannelResource resource = null;
        if(model != null) {
            resource = new ChannelResource();
            resource.setChannelCode(model.getChannelCode());
            resource.setEnabled(model.getEnabled());
            resource.setBrokerDescription(model.getBrokerDescription());
        }
        return resource;
    }

    public static ChannelsResource toResource(Channels model) {
        ChannelsResource resource = null;
        if(model != null) {
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

    public static WrapperChannelDetailsResource toResource(WrapperEntityOperations<ChannelDetails> wrapperModel, PspChannelPaymentTypes listModel) {
        WrapperChannelDetailsResource resource = null;
        if(wrapperModel != null) {
            ChannelDetails model = wrapperModel.getEntity();
            resource = new WrapperChannelDetailsResource();
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
            resource.setNmpService(model.getNmpService());
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
            resource.setPaymentTypeList(listModel != null ? listModel.getPaymentTypeList() : new ArrayList<>());

            resource.setId(wrapperModel.getId());
            resource.setType(wrapperModel.getType());
            resource.setCreatedAt(wrapperModel.getCreatedAt());
            resource.setModifiedAt(wrapperModel.getModifiedAt());
            resource.setModifiedBy(wrapperModel.getModifiedBy());
            resource.setModifiedByOpt(wrapperModel.getModifiedByOpt());
            resource.setNote(wrapperModel.getNote());
        }
        return resource;
    }

    public static ChannelDetailsResource toResource(ChannelDetails model, PspChannelPaymentTypes listModel) {
        ChannelDetailsResource resource = null;
        if(model != null) {
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
            resource.setNmpService(model.getNmpService());
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
            resource.setTargetHostNmp(model.getTargetHostNmp());
            resource.setTargetPortNmp(model.getTargetPortNmp());
            resource.setTargetPathNmp(model.getTargetPathNmp());
            resource.setPaymentTypeList(listModel != null ? listModel.getPaymentTypeList() : new ArrayList<>());
        }
        return resource;
    }

    public static ChannelDetailsResource toResource(ChannelDetails model, PspChannelPaymentTypes listModel, WrapperStatus status, String createdBy, String modifiedBy) {
        ChannelDetailsResource resource = null;
        if(model != null) {
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
            resource.setNmpService(model.getNmpService());
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
            resource.setTargetHostNmp(model.getTargetHostNmp());
            resource.setTargetPortNmp(model.getTargetPortNmp());
            resource.setTargetPathNmp(model.getTargetPathNmp());
            resource.setPaymentTypeList(listModel != null ? listModel.getPaymentTypeList() : new ArrayList<>());
            resource.setWrapperStatus(status);
            resource.setCreatedBy(createdBy);
            resource.setModifiedBy(modifiedBy);
            resource.setModifiedAt(model.getModifiedAt());
            resource.setCreatedAt(model.getCreatedAt());
        }
        return resource;
    }

    public static ChannelDetailsResource toResource(ChannelDetails model) {
        ChannelDetailsResource resource = null;
        if(model != null) {
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
            resource.setNmpService(model.getNmpService());
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
            resource.setTargetHostNmp(model.getTargetHostNmp());
            resource.setTargetPortNmp(model.getTargetPortNmp());
            resource.setTargetPathNmp(model.getTargetPathNmp());
        }
        return resource;
    }

    public static ChannelDetails fromChannelDetailsDto(ChannelDetailsDto model) {
        ChannelDetails resource = null;
        if(model != null) {
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
            resource.setNmpService(model.getNmpService());
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
            resource.setFlagPspCp(model.getFlagPspCp());
            resource.setPaymentTypeList(model.getPaymentTypeList());
        }
        return resource;
    }

    public static ChannelDetails fromWrapperChannelDetailsDto(WrapperChannelDetailsDto model) {
        ChannelDetails resource = null;
        if(model != null) {
            resource = new ChannelDetails();
            resource.setBrokerPspCode(model.getBrokerPspCode());
            resource.setTargetHost(model.getTargetHost());
            resource.setTargetPort(model.getTargetPort());
            resource.setTargetPath(model.getTargetPath());
            resource.setRedirectIp(model.getRedirectIp());
            resource.setRedirectPath(model.getRedirectPath());
            resource.setRedirectPort(model.getRedirectPort());
            resource.setRedirectQueryString(model.getRedirectQueryString());
            resource.setRedirectProtocol(model.getRedirectProtocol());
            resource.setBrokerDescription(model.getBrokerDescription());
            resource.setChannelCode(model.getChannelCode());
            resource.setPaymentTypeList(model.getPaymentTypeList());
            //default
            resource.setThreadNumber(1L);
            resource.setTimeoutA(15L);
            resource.setTimeoutB(30L);
            resource.setTimeoutC(120L);
        }
        return resource;
    }

    public static PspChannelResource toResource(PspChannel model) {
        PspChannelResource resource = null;
        if(model != null) {
            resource = new PspChannelResource();
            resource.setChannelCode(model.getChannelCode());
            resource.setEnabled(model.getEnabled());
            resource.setPaymentTypeList(model.getPaymentTypeList());
        }
        return resource;
    }

    public static PspChannelsResource toResource(PspChannels model) {
        List<PspChannelResource> channelResourceList = new ArrayList<>();
        PspChannelsResource resource = null;
        if(model != null) {
            resource = new PspChannelsResource();
            List<PspChannel> channels = model.getChannelsList();
            channels.forEach(pspChannel ->
                    channelResourceList.add(toResource(pspChannel))
            );
            resource.setChannelsList(channelResourceList);
        }
        return resource;
    }

    public static PspChannelPaymentTypesResource toResource(PspChannelPaymentTypes model) {
        PspChannelPaymentTypesResource resource = null;
        if(model != null) {
            resource = new PspChannelPaymentTypesResource();
            resource.setPaymentTypeList(model.getPaymentTypeList());
        }
        return resource;
    }

    public static PaymentServiceProviderResource toResource(PaymentServiceProvider model) {
        PaymentServiceProviderResource resource = null;
        if(model != null) {
            resource = new PaymentServiceProviderResource();
            resource.setEnabled(model.getEnabled());
            resource.setBusinessName(model.getBusinessName());
            resource.setPspCode(model.getPspCode());
        }
        return resource;
    }

    public static PaymentServiceProvidersResource toResource(PaymentServiceProviders model) {
        PaymentServiceProvidersResource resource = null;
        List<PaymentServiceProviderResource> paymentServiceProviderResourceList = new ArrayList<>();
        if(model != null) {
            resource = new PaymentServiceProvidersResource();
            resource.setPageInfo(model.getPageInfo());
            if(model.getPaymentServiceProviderList() != null) {
                model.getPaymentServiceProviderList().forEach(i -> paymentServiceProviderResourceList.add(toResource(i)));
            }
            resource.setPageInfo(model.getPageInfo());
            resource.setPaymentServiceProviderList(paymentServiceProviderResourceList);

        }
        return resource;
    }

    public static ChannelPspResource toResource(ChannelPsp model) {
        ChannelPspResource resource = null;
        List<String> list = new ArrayList<>();
        if(model != null) {
            resource = new ChannelPspResource();
            if(model.getPaymentTypeList() != null) {
                model.getPaymentTypeList().forEach(i -> list.add(i));
            }
            resource.setEnabled(model.getEnabled());
            resource.setBusinessName(model.getBusinessName());
            resource.setPspCode(model.getPspCode());
            resource.setPaymentTypeList(list);
        }
        return resource;
    }

    public static ChannelPspListResource toResource(ChannelPspList model) {
        ChannelPspListResource resource = null;
        List<ChannelPspResource> list = new ArrayList<>();
        if(model != null) {
            resource = new ChannelPspListResource();
            if(model.getPsp() != null) {
                model.getPsp().forEach(i -> list.add(toResource(i)));
            }
            resource.setPageInfo(model.getPageInfo());
            resource.setPsp(list);

        }
        return resource;
    }

    public static BrokerPspDetailsResource toResource(BrokerPspDetails model) {
        BrokerPspDetailsResource resource = null;
        if(model != null) {
            resource = new BrokerPspDetailsResource();

            resource.setDescription(model.getDescription());
            resource.setEnabled(model.getEnabled());
            resource.setBrokerPspCode(model.getBrokerPspCode());
            resource.setExtendedFaultBean(model.getExtendedFaultBean());
        }
        return resource;
    }

    public static BrokerPspDetails fromBrokerPspDetailsDto(BrokerPspDetailsDto dto) {
        BrokerPspDetails model = null;
        if(dto != null) {
            model = new BrokerPspDetails();


            model.setDescription(dto.getDescription());
            model.setEnabled(dto.getEnabled());
            model.setBrokerPspCode(dto.getBrokerPspCode());
            model.setExtendedFaultBean(dto.getExtendedFaultBean());
        }
        return model;
    }


    public static PaymentServiceProviderDetails fromPaymentServiceProviderDetailsDto(PaymentServiceProviderDetailsDto dto) {
        PaymentServiceProviderDetails model = null;
        if(dto != null) {
            model = new PaymentServiceProviderDetails();
            model.setAbi(dto.getAbi());
            model.setBic(dto.getBic());
            model.setStamp(dto.getStamp());
            model.setAgidPsp(dto.getAgidPsp());
            model.setMyBankCode(dto.getMyBankCode());
            model.setVatNumber(dto.getVatNumber());
            model.setMyBankCode(dto.getMyBankCode());
            model.setPspCode(dto.getPspCode());
            model.setBusinessName(dto.getBusinessName());
            model.setEnabled(dto.getEnabled());
            model.setTaxCode(dto.getTaxCode());
        }
        return model;
    }

    public static BrokerPspDetails fromPaymentServiceProviderDetailsDtoToMap(PaymentServiceProviderDetailsDto dto) {
        BrokerPspDetails modelBrokerPsp = null;
        if(dto != null) {
            modelBrokerPsp = new BrokerPspDetails();


            modelBrokerPsp.setBrokerPspCode(dto.getTaxCode());
            modelBrokerPsp.setEnabled(dto.getEnabled());
            modelBrokerPsp.setDescription(dto.getBusinessName());
            modelBrokerPsp.setExtendedFaultBean(true);
        }

        return modelBrokerPsp;
    }

    public static PaymentServiceProviderDetailsResource toResource(PaymentServiceProviderDetails model) {
        PaymentServiceProviderDetailsResource resource = null;

        if(model != null) {
            resource = new PaymentServiceProviderDetailsResource();

            resource.setAbi(model.getAbi());
            resource.setBic(model.getBic());
            resource.setStamp(model.getStamp());
            resource.setAgidPsp(model.getAgidPsp());
            resource.setMyBankCode(model.getMyBankCode());
            resource.setVatNumber(model.getVatNumber());
            resource.setMyBankCode(model.getMyBankCode());
            resource.setPspCode(model.getPspCode());
            resource.setBusinessName(model.getBusinessName());
            resource.setEnabled(model.getEnabled());
            resource.setTaxCode(model.getTaxCode());


        }
        return resource;
    }

    public static WrapperChannel toWrapperChannel(Channel model) {
        if(model == null) {
            return null;
        }

        WrapperChannel wrapperChannel = new WrapperChannel();

        wrapperChannel.setChannelCode(model.getChannelCode());
        wrapperChannel.setEnabled(model.getEnabled());
        wrapperChannel.setBrokerDescription(model.getBrokerDescription());
        wrapperChannel.setWrapperStatus(WrapperStatus.APPROVED);

        return wrapperChannel;
    }

    public static WrapperChannels toWrapperChannels(Channels model) {
        if(model == null) {
            return null;
        }

        WrapperChannels wrapperChannels = new WrapperChannels();

        wrapperChannels.setChannelList(model.getChannelList().stream()
                .map(ChannelMapper::toWrapperChannel)
                .collect(Collectors.toList()));
        wrapperChannels.setPageInfo(model.getPageInfo());

        return wrapperChannels;
    }


    public static WrapperChannel toWrapperChannel(WrapperEntityOperations<ChannelDetails> wrapperEntityOperations) {
        if(wrapperEntityOperations == null) {
            return null;
        }

        WrapperChannel wrapperChannel = new WrapperChannel();

        wrapperChannel.setChannelCode(wrapperEntityOperations.getEntity().getChannelCode());
        wrapperChannel.setEnabled(wrapperEntityOperations.getEntity().getEnabled());
        wrapperChannel.setBrokerDescription(wrapperEntityOperations.getEntity().getBrokerDescription());

        wrapperChannel.setWrapperStatus(wrapperEntityOperations.getStatus());
        wrapperChannel.setCreatedAt(wrapperEntityOperations.getCreatedAt());
        wrapperChannel.setModifiedAt(wrapperEntityOperations.getModifiedAt());

        return wrapperChannel;

    }

    public static WrapperChannels toWrapperChannels(WrapperEntitiesList wrapperEntitiesList) {
        if(wrapperEntitiesList == null) {
            return null;
        }

        WrapperChannels wrapperChannels = new WrapperChannels();
        List<WrapperChannel> channelList = new ArrayList<>();

        wrapperEntitiesList.getWrapperEntities().forEach(
                ent -> channelList.add(toWrapperChannel(
                        (WrapperEntityOperations<ChannelDetails>) getWrapperEntityOperationsSortedList(ent).get(0))));

        wrapperChannels.setChannelList(channelList);
        wrapperChannels.setPageInfo(wrapperEntitiesList.getPageInfo());

        return wrapperChannels;
    }

    public static WrapperChannelsResource toWrapperChannelsResource(WrapperChannels wrapperChannels) {
        if(wrapperChannels == null) {
            return null;
        }

        WrapperChannelsResource wrapperChannelsResource = new WrapperChannelsResource();

        wrapperChannelsResource.setChannelList(wrapperChannels.getChannelList().stream()
                .map(station -> toWrapperChannelResource(station))
                .collect(Collectors.toList()));
        wrapperChannelsResource.setPageInfo(wrapperChannels.getPageInfo());

        return wrapperChannelsResource;
    }

    public static WrapperChannelResource toWrapperChannelResource(WrapperChannel wrapperChannel) {
        if(wrapperChannel == null) {
            return null;
        }

        WrapperChannelResource wrapperChannelResource = new WrapperChannelResource();

        wrapperChannelResource.setChannelCode(wrapperChannel.getChannelCode());
        wrapperChannelResource.setBrokerDescription(wrapperChannel.getBrokerDescription());
        wrapperChannelResource.setEnabled(wrapperChannel.getEnabled());
        wrapperChannelResource.setWrapperStatus(wrapperChannel.getWrapperStatus());
        wrapperChannelResource.setModifiedAt(wrapperChannel.getModifiedAt());
        wrapperChannelResource.setCreatedAt(wrapperChannel.getCreatedAt());

        return wrapperChannelResource;

    }


    public static ChannelDetailsResourceList fromChannelDetailsList(ChannelDetailsList model) {
        if(model == null) {
            return null;
        }
        ChannelDetailsResourceList resource = new ChannelDetailsResourceList();
        resource.setChannelDetailsResources(model.getChannelDetailsList().stream().map(ChannelMapper::toResource).collect(Collectors.toList()));

        resource.setPageInfo(model.getPageInfo());
        return resource;
    }
}
