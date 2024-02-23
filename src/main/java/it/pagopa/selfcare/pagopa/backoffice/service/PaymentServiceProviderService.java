package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerOrPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.BrokerPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.ChannelCodeResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProviderDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProviderDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PaymentServiceProvidersResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelPaymentTypesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.PspChannelsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channel;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.Channels;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviders;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannelPaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PspChannels;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

@Service
@Slf4j
public class PaymentServiceProviderService {

    private final ApiConfigClient apiConfigClient;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final WrapperService wrapperService;

    private final ModelMapper modelMapper;

    private final LegacyPspCodeUtil legacyPspCodeUtil;


    @Autowired
    public PaymentServiceProviderService(
            ApiConfigClient apiConfigClient,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            WrapperService wrapperService,
            ModelMapper modelMapper,
            LegacyPspCodeUtil legacyPspCodeUtil) {
        this.apiConfigClient = apiConfigClient;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.wrapperService = wrapperService;
        this.modelMapper = modelMapper;
        this.legacyPspCodeUtil = legacyPspCodeUtil;
    }

    public PaymentServiceProviderDetailsResource createPSP(
            PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto, Boolean isDirect) {
        BrokerPspDetails brokerPspDetails = ChannelMapper.fromPaymentServiceProviderDetailsDtoToMap(
                paymentServiceProviderDetailsDto);
        if (Boolean.TRUE.equals(isDirect)) {
            apiConfigClient.createBrokerPsp(brokerPspDetails);
        }

        if (StringUtils.isNotEmpty(paymentServiceProviderDetailsDto.getAbi())) {
            paymentServiceProviderDetailsDto.setPspCode("ABI".concat(paymentServiceProviderDetailsDto.getAbi()));
        } else if (StringUtils.isNotEmpty(paymentServiceProviderDetailsDto.getBic())) {
            paymentServiceProviderDetailsDto.setPspCode(paymentServiceProviderDetailsDto.getBic());
        } else {
            throw new AppException(AppError.BAD_REQUEST, "Missing ABI/BIC while creating new psp for %s",
                    paymentServiceProviderDetailsDto.getTaxCode());
        }

        PaymentServiceProviderDetails pspDetails = modelMapper.map(
                paymentServiceProviderDetailsDto, PaymentServiceProviderDetails.class);
        PaymentServiceProviderDetails responsePSP = apiConfigClient.createPaymentServiceProvider(pspDetails);
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

    public BrokerOrPspDetailsResource getBrokerAndPspDetails(String taxCode) {
        String brokerPspCode = legacyPspCodeUtil.retrievePspCode(taxCode, false);

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
            throw new AppException(AppError.ACTOR_NOT_FOUND, brokerPspCode);
        }

        BrokerOrPspDetailsResource resource = new BrokerOrPspDetailsResource();
        resource.setBrokerPspDetailsResource(brokerPspDetailsResource);
        resource.setPaymentServiceProviderDetailsResource(paymentServiceProviderDetailsResource);

        return resource;
    }


    public PspChannelsResource getPSPChannels(String pspTaxCode) {
        PspChannels dto = apiConfigSelfcareIntegrationClient.getPspChannels(pspTaxCode);
        return ChannelMapper.toResource(dto);
    }

    public PspChannelPaymentTypesResource updatePSPChannel(String taxCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes) {
        String pspCode = legacyPspCodeUtil.retrievePspCode(taxCode, false);
        PspChannelPaymentTypes dto = apiConfigClient.updatePaymentServiceProvidersChannels(pspCode, channelCode, pspChannelPaymentTypes);
        return ChannelMapper.toResource(dto);
    }

    public void deletePSPChannel(String pspCode, String channelCode) {
        apiConfigClient.deletePaymentServiceProvidersChannels(pspCode, channelCode);
    }


    public ChannelCodeResource getFirstValidChannelCode(String pspCode, Boolean v2) {
        if(Boolean.TRUE.equals(v2)) {
            return new ChannelCodeResource(wrapperService.getFirstValidCodeV2(pspCode));
        } else {
            return new ChannelCodeResource(getFirstValidChannelCodeAux(pspCode));
        }
    }

    private String getFirstValidChannelCodeAux(String pspCode) {
        Channels response = apiConfigClient.getChannels(1, 0, pspCode, null, "DESC");
        List<Channel> codeList = response.getChannelList();
        Set<String> codes = codeList.stream().map(Channel::getChannelCode)
                .filter(s -> s.matches(REGEX_GENERATE)) // String_nn
                .collect(Collectors.toSet());
        return generator(codes, pspCode);
    }
}
