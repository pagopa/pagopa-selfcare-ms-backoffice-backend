package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.ResourceNotFoundException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperType;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

@Service
@Slf4j
public class PaymentServiceProviderService {

    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private WrapperService wrapperService;

    @Autowired
    private ModelMapper modelMapper;

    public PaymentServiceProviderDetailsResource createPSP(PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto, Boolean direct) {
        var dtoAsMap = ChannelMapper.fromPaymentServiceProviderDetailsDtoToMap(paymentServiceProviderDetailsDto);
        if(Boolean.TRUE.equals(direct)) {
            apiConfigClient.createBrokerPsp(dtoAsMap);
        }
        PaymentServiceProviderDetails responsePSP = apiConfigClient.createPaymentServiceProvider(modelMapper.map(paymentServiceProviderDetailsDto, PaymentServiceProviderDetails.class));
        return ChannelMapper.toResource(responsePSP);
    }

    public PaymentServiceProviderDetailsResource updatePSP(String pspcode, @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {

        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);
        PaymentServiceProviderDetails response = apiConfigClient.updatePSP(pspcode, paymentServiceProviderDetails);
        return ChannelMapper.toResource(response);
    }

    public PaymentServiceProvidersResource getPaymentServiceProviders(Integer limit, Integer page, String pspCode, String taxCode, String name) {

        PaymentServiceProviders response = apiConfigClient.getPaymentServiceProviders(limit, page, pspCode, name, taxCode);
        return ChannelMapper.toResource(response);
    }

    public BrokerOrPspDetailsResource getBrokerAndPspDetails(String brokerPspCode) {
        BrokerPspDetailsResource brokerPspDetailsResource = null;
        PaymentServiceProviderDetailsResource paymentServiceProviderDetailsResource = null;
        BrokerPspDetails brokerPspDetails;
        PaymentServiceProviderDetails paymentServiceProviderDetails;
        try {
            brokerPspDetails = apiConfigClient.getBrokerPsp(brokerPspCode);
            brokerPspDetailsResource = ChannelMapper.toResource(brokerPspDetails);
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerAndPspDetails - Not BrokerPSP found");
        }

        try {
            paymentServiceProviderDetails = apiConfigClient.getPSPDetails(brokerPspCode);
            paymentServiceProviderDetailsResource = ChannelMapper.toResource(paymentServiceProviderDetails);
        } catch (FeignException.NotFound e) {
            log.trace("getBrokerAndPspDetails - Not PaymentServiceProvider found");
        }

        if(brokerPspDetailsResource == null && paymentServiceProviderDetailsResource == null) {
            throw new ResourceNotFoundException("Nessun dato trovato per il broker o per il creditorInstitution");
        }

        BrokerOrPspDetailsResource resource = new BrokerOrPspDetailsResource();
        resource.setBrokerPspDetailsResource(brokerPspDetailsResource);
        resource.setPaymentServiceProviderDetailsResource(paymentServiceProviderDetailsResource);

        return resource;
    }


    public PspChannelsResource getPSPChannels(String pspCode) {
        PspChannels dto = apiConfigClient.getPspChannels(pspCode);
        return ChannelMapper.toResource(dto);
    }

    public PspChannelPaymentTypesResource updatePSPChannel(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes) {
        PspChannelPaymentTypes dto = apiConfigClient.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypes);
        return ChannelMapper.toResource(dto);
    }

    public void deletePSPChannel(String pspCode, String channelCode) {
        apiConfigClient.deletePaymentServiceProvidersChannels(pspCode, channelCode);
    }


    public ChannelCodeResource getFirstValidChannelCode(String pspCode, Boolean v2) {
        if(Boolean.TRUE.equals(v2)) {
            return getFirstValidChannelCodeV2(pspCode);
        } else {
            return new ChannelCodeResource(getFirstValidChannelCodeAux(pspCode));
        }
    }

    public ChannelCodeResource getFirstValidChannelCodeV2(String pspCode) {
        Channels channels = apiConfigClient.getChannels(1, 0, pspCode, null, "DESC");
        WrapperEntitiesList mongoList = wrapperService.findByIdLikeOrTypeOrBrokerCode(pspCode, WrapperType.CHANNEL, null, 0, 100);
        WrapperChannels channelsMergedAndSorted = Utility.mergeAndSortWrapperChannels(
                ChannelMapper.toWrapperChannels(channels),
                ChannelMapper.toWrapperChannels(mongoList),
                "ASC");
        String result = generateChannelCodeV2(channelsMergedAndSorted.getChannelList(), pspCode);
        return new ChannelCodeResource(result);
    }


    private String getFirstValidChannelCodeAux(String pspCode) {
        Channels response = apiConfigClient.getChannels(1, 0, pspCode, null, "DESC");
        List<Channel> codeList = response.getChannelList();
        List<String> codes = codeList.stream().map(Channel::getChannelCode)
                .filter(s -> s.matches(REGEX_GENERATE)) // String_nn
                .collect(Collectors.toList());
        return generator(codes, pspCode);
    }

    private String generateChannelCodeV2(List<WrapperChannel> stationList, String ecCode) {
        List<String> codes = stationList.stream().map(WrapperChannel::getChannelCode)
                .filter(s -> s.matches(REGEX_GENERATE))
                .collect(Collectors.toList());
        return generator(codes, ecCode);
    }

}
