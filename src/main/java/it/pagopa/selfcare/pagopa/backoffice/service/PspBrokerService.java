package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.model.channels.*;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.BrokersPsp;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.ChannelDetailsList;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.channel.PaymentServiceProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class PspBrokerService {


    @Autowired
    private ApiConfigClient apiConfigClient;

    @Autowired
    private ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;


    public BrokersPspResource getBrokersForPSP(Integer limit, Integer page, String filterByCode, String filterByName, String orderBy, String sorting) {
        BrokersPsp dto = apiConfigClient.getBrokersPsp(limit, page, filterByCode, filterByName, orderBy, sorting);
        return ChannelMapper.toResource(dto);
    }

    public BrokerPspDetailsResource getBrokerForPsp(String brokerPspCode) {
        BrokerPspDetails dto = apiConfigClient.getBrokerPsp(brokerPspCode);
        return ChannelMapper.toResource(dto);
    }

    public BrokerPspDetailsResource createBrokerForPSP(BrokerPspDetailsDto brokerPspDetailsDto) {
        BrokerPspDetails dto = ChannelMapper.fromBrokerPspDetailsDto(brokerPspDetailsDto);
        dto = apiConfigClient.createBrokerPsp(dto);
        return ChannelMapper.toResource(dto);
    }

    public BrokerPspDetailsResource updateBrokerPSP(String brokerCode, @NotNull BrokerPspDetailsDto brokerPspDetailsDto) {

        BrokerPspDetails brokerPspDetails = ChannelMapper.fromBrokerPspDetailsDto(brokerPspDetailsDto);
        BrokerPspDetails response = apiConfigClient.updateBrokerPSP(brokerCode, brokerPspDetails);
        return ChannelMapper.toResource(response);
    }


    public ChannelDetailsResourceList getChannelByBroker(String brokerId, String channelId, Integer limit, Integer page) {
        ChannelDetailsList dto = apiConfigSelfcareIntegrationClient.getChannelDetailsListByBroker(brokerId, channelId, limit, page);
        return ChannelMapper.fromChannelDetailsList(dto);
    }


    public PaymentServiceProvidersResource getPSPAssociatedToBroker(String brokerPspCode, Integer limit, Integer page) {
        PaymentServiceProviders dto = apiConfigClient.getPspBrokerPsp(limit, page, brokerPspCode);
        return ChannelMapper.toResource(dto);
    }


}
