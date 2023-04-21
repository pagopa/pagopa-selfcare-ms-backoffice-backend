package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            resource.setPaymentTypeList(listModel != null ? listModel.getPaymentTypeList() : new ArrayList<>());
        }
        return resource;
    }

    public static ChannelDetailsResource toResource(ChannelDetails model) {
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

    public static ChannelDetails fromWrapperChannelDetailsDto(WrapperChannelDetailsDto model) {
        ChannelDetails resource = null;
        if (model != null) {
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

        }
        return resource;
    }
    public static PaymentTypesResource toResource(PaymentTypes model) {
        PaymentTypesResource resource = null;
        List<PaymentTypeResource> paymentTypeResourceList = new ArrayList<>();
        if (model != null) {
            resource = new PaymentTypesResource();
            List<PaymentType> paymentTypeList = model.getPaymentTypeList();
            if (paymentTypeList != null) {
                for (PaymentType paymentType : paymentTypeList) {
                    PaymentTypeResource paymentTypeResource = new PaymentTypeResource();
                    paymentTypeResource.setDescription(paymentType.getDescription());
                    paymentTypeResource.setPaymentTypeCode(paymentType.getPaymentTypeCode());

                    paymentTypeResourceList.add(paymentTypeResource);

                }
                resource.setPaymentTypeList(paymentTypeResourceList);
            }

        }
        return resource;
    }

    public static PspChannelResource toResource(PspChannel model) {
        PspChannelResource resource = null;
        if (model != null) {
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
        if (model != null) {
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
        if (model != null) {
            resource = new PspChannelPaymentTypesResource();
            resource.setPaymentTypeList(model.getPaymentTypeList());
        }
        return resource;
    }

    public static PaymentServiceProviderResource toResource(PaymentServiceProvider model) {
        PaymentServiceProviderResource resource = null;
        if (model != null) {
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
        if (model != null) {
            resource = new PaymentServiceProvidersResource();
            resource.setPageInfo(model.getPageInfo());
            if (model.getPaymentServiceProviderList() != null) {
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
        if (model != null) {
            resource = new ChannelPspResource();
            if (model.getPaymentTypeList() != null) {
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
        if (model != null) {
            resource = new ChannelPspListResource();
            if (model.getPsp() != null) {
                model.getPsp().forEach(i -> list.add(toResource(i)));
            }
            resource.setPageInfo(model.getPageInfo());
            resource.setPsp(list);

        }
        return resource;
    }

    public static BrokerPspDetailsResource toResource(BrokerPspDetails model) {
        BrokerPspDetailsResource resource = null;
        if (model != null) {
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
        if (dto != null) {
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
        if (dto != null) {
            model = new PaymentServiceProviderDetails();
            model.setAbi(dto.getAbi());
            model.setBic(dto.getBic());
            model.setStamp(dto.getStamp());
            model.setTransfer(dto.getTransfer());
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

    public static Map<String, Object> fromPaymentServiceProviderDetailsDtoToMap(PaymentServiceProviderDetailsDto dto) {
        PaymentServiceProviderDetails model = null;
        BrokerPspDetails modelBrokerPsp = null;
        Map<String,Object> result = new HashMap<>();
        if (dto != null) {
            model = new PaymentServiceProviderDetails();
            modelBrokerPsp = new BrokerPspDetails();

            model.setAbi(dto.getAbi());
            model.setBic(dto.getBic());
            model.setStamp(dto.getStamp());
            model.setTransfer(dto.getTransfer());
            model.setAgidPsp(dto.getAgidPsp());
            model.setMyBankCode(dto.getMyBankCode());
            model.setVatNumber(dto.getVatNumber());
            model.setMyBankCode(dto.getMyBankCode());
            model.setPspCode(dto.getPspCode());
            model.setBusinessName(dto.getBusinessName());
            model.setEnabled(dto.getEnabled());
            model.setTaxCode(dto.getTaxCode());

            modelBrokerPsp.setBrokerPspCode(dto.getPspCode());
            modelBrokerPsp.setEnabled(dto.getEnabled());
            modelBrokerPsp.setDescription(dto.getBusinessName());
            modelBrokerPsp.setExtendedFaultBean(true);
        }
        result.put("psp",model);
        result.put("broker",modelBrokerPsp);

        return result;
    }
    public static PaymentServiceProviderDetailsResource toResource(PaymentServiceProviderDetails model) {
        PaymentServiceProviderDetailsResource resource = null;

        if (model != null) {
            resource = new PaymentServiceProviderDetailsResource();

            resource.setAbi(model.getAbi());
            resource.setBic(model.getBic());
            resource.setStamp(model.getStamp());
            resource.setTransfer(model.getTransfer());
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
}
