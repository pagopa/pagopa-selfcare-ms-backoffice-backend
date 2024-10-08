package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.REGEX_GENERATE;
import static it.pagopa.selfcare.pagopa.backoffice.util.StringUtils.generator;

/**
 * Service that manage Payment Service Providers
 */
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

    public PaymentServiceProviderDetailsResource createPSP(PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto, Boolean isDirect) {
        BrokerPspDetails brokerPspDetails = ChannelMapper.fromPaymentServiceProviderDetailsDtoToMap(
                paymentServiceProviderDetailsDto);
        if(Boolean.TRUE.equals(isDirect)) {
            apiConfigClient.createBrokerPsp(brokerPspDetails);
        }

        if(StringUtils.isNotEmpty(paymentServiceProviderDetailsDto.getAbi()) && !paymentServiceProviderDetailsDto.getAbi().equals("N/A")) {
            paymentServiceProviderDetailsDto.setPspCode("ABI".concat(paymentServiceProviderDetailsDto.getAbi()));
        } else if(StringUtils.isNotEmpty(paymentServiceProviderDetailsDto.getBic())) {
            paymentServiceProviderDetailsDto.setPspCode(paymentServiceProviderDetailsDto.getBic());
        } else {
            throw new AppException(AppError.BAD_REQUEST, "Missing ABI/BIC while creating new psp for %s",
                    paymentServiceProviderDetailsDto.getTaxCode());
        }
        PaymentServiceProviderDetails pspDetails = modelMapper.map(paymentServiceProviderDetailsDto, PaymentServiceProviderDetails.class);

        legacyPspCodeUtil.upsertPspLegacy(pspDetails);

        PaymentServiceProviderDetails responsePSP = apiConfigClient.createPaymentServiceProvider(pspDetails);
        return ChannelMapper.toResource(responsePSP);
    }

    public PaymentServiceProviderDetailsResource updatePSP(String pspTaxCode, @NotNull PaymentServiceProviderDetailsDto paymentServiceProviderDetailsDto) {
        PaymentServiceProviderDetails paymentServiceProviderDetails = ChannelMapper.fromPaymentServiceProviderDetailsDto(paymentServiceProviderDetailsDto);
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);

        legacyPspCodeUtil.upsertPspLegacy(paymentServiceProviderDetails);

        PaymentServiceProviderDetails response = this.apiConfigClient.updatePSP(pspCode, paymentServiceProviderDetails);
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

    public void dissociatePSPFromChannel(String pspTaxCode, String channelCode) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, true);
        this.apiConfigClient.deletePaymentServiceProvidersChannels(pspCode, channelCode);
    }


    public ChannelCodeResource getFirstValidChannelCode(String taxCode, Boolean v2) {
        if(Boolean.TRUE.equals(v2)) {
            return new ChannelCodeResource(this.wrapperService.getFirstValidCodeV2(taxCode));
        } else {
            return new ChannelCodeResource(getFirstValidChannelCodeAux(taxCode));
        }
    }

    /**
     * @deprecated this API invoke the old station code generation logic that can cause collision on wrapper data for PT
     */
    @Deprecated(forRemoval = true)
    private String getFirstValidChannelCodeAux(String taxCode) {
        Channels response = apiConfigClient.getChannels(null, taxCode, "DESC", 1, 0);
        List<Channel> codeList = response.getChannelList();
        Set<String> codes = codeList.stream().map(Channel::getChannelCode)
                .filter(s -> s.matches(REGEX_GENERATE)) // String_nn
                .collect(Collectors.toSet());
        return generator(codes, taxCode);
    }


}
